package org.example.managers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


import org.example.LocalDateAdapter;
import org.example.Request;
import org.example.User;
import org.example.commands.Command;
import org.example.db.DatabaseManager;
import org.example.exceptions.ExitException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.utility.ProcessingTask;

/**
 * Класс Runner. Отвечает за последовательный запуск команд,
 * получаемых через DatagramSocket
 */
public class Runner {

    private CommandManager commandManager;
    private final DatagramSocket socket;
    private final Gson gson;
    private static final int BUFFER_SIZE = 65536;
    private static final int SOCKET_TIMEOUT = 60000;
    private boolean isRunning;
    private AuthManager authManager;
    private ExecutorService pool; // пул потоков чтобы не прям многа
    private final ForkJoinPool processPool;
    private final ReadWriteLock cacheLock = new ReentrantReadWriteLock();

    public Runner(CommandManager commandManager, DatagramSocket socket, DatabaseManager databaseManager) {
        this.commandManager = commandManager;
        this.socket = socket;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .create();
        this.isRunning = true;
        this.authManager = new AuthManager(databaseManager);

        try {
            this.socket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (SocketException e) {
            System.err.println("Не удалось установить таймаут для сокета: " + e.getMessage());
        }
        this.pool = Executors.newFixedThreadPool(25);
        this.processPool = new ForkJoinPool(4);
    }

    public void run() throws IOException {
        System.out.println("Сервер запущен и ожидает команды...");

        while (isRunning) {
            try {
                // Для каждого пакета свой буфер
                byte[] receiveBuffer = new byte[BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                // Принимаем пакет (главный поток)
                socket.receive(receivePacket);

                // Сохраняем данные перед отправкой в пул
                final InetAddress clientAddress = receivePacket.getAddress();
                final int clientPort = receivePacket.getPort();
                final byte[] dataCopy = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());

                pool.submit(() -> {
                    try {
                        handleRequest(dataCopy, clientAddress, clientPort);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

            } catch (SocketTimeoutException e) {
                continue;
            } catch (SocketException e) {
                if (isRunning) {
                    System.out.println("Сокет был закрыт: " + e.getMessage());
                }
                return;
            } catch (IOException e) {
                System.err.println("Ошибка ввода-вывода: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Неожиданная ошибка: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Выполнение базовой команды
     */
    private void handleRequest(byte[] data, InetAddress clientAddress, int clientPort) throws IOException {
        try {
            // Парсим JSON
            String jsonData = new String(data, "UTF-8");
            Request clientRequest = gson.fromJson(jsonData, Request.class);

            if (clientRequest == null) {
                sendErrorResponse(clientAddress, clientPort, "Неверный формат запроса");
                return;
            }

            System.out.println("[" + Thread.currentThread().getName() + "] Получена команда: " +
                    clientRequest.getCommandName() + " от " + clientAddress + ":" + clientPort);

            ProcessingTask task = new ProcessingTask(clientRequest, clientAddress, clientPort, this);
            processPool.execute(task); // Асинхронно, не блокируем

        } catch (UnsupportedEncodingException e) {
            sendErrorResponse(clientAddress, clientPort, "Ошибка кодировки");
        } catch (Exception e) {
            System.err.println("Ошибка парсинга: " + e.getMessage());
            sendErrorResponse(clientAddress, clientPort, "Внутренняя ошибка сервера");
        }
    }

    /**
     * Отправляет ответ клиенту
     */
    public void sendResponse(java.net.InetAddress address, int port, Request response) throws IOException {
        Thread senderThread = new Thread(() -> {
            try {
                String jsonResponse = gson.toJson(response);
                byte[] data = jsonResponse.getBytes("UTF-8");

                if (data.length > BUFFER_SIZE) {
                    response.setCommandName("error");
                    response.setData("Ответ слишком большой для отправки по UDP");
                    jsonResponse = gson.toJson(response);
                    data = jsonResponse.getBytes("UTF-8");
                }

                DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
                socket.send(packet);

                System.out.println("[" + Thread.currentThread().getName() + "] Отправлен ответ: " +
                        response.getCommandName());

            } catch (IOException e) {
                System.err.println("[" + Thread.currentThread().getName() + "] Ошибка отправки: " + e.getMessage());
            }
        });

        senderThread.setName("ResponseSender-" + address.getHostAddress() + ":" + port);
        senderThread.start();
    }

    /**
     * Отправляет ошибку клиенту
     */
    private void sendErrorResponse(java.net.InetAddress address, int port, String errorMessage) throws IOException {

        Request errorResponse = new Request();
        errorResponse.setCommandName("error");
        errorResponse.setData(errorMessage);
        errorResponse.setTimestamp(System.currentTimeMillis());
        sendResponse(address, port, errorResponse);
    }

    /**
     * Обрабатывает запрос от клиента
     */
    public Request processRequest(Request request) {
        Request response = new Request();
        response.setTimestamp(System.currentTimeMillis());

        try {
            String commandName = request.getCommandName();
            String[] args = request.getArgs();

            if (commandName == null || commandName.trim().isEmpty()) {
                response.setCommandName("error");
                response.setData("Команда не может быть пустой");
                return response;
            }

            commandName = commandName.toLowerCase();

            // Специальная обработка для auth и register
            if (commandName.equals("auth")) {
                return handleAuth(request);
            }
            if (commandName.equals("register")) {
                return handleRegister(request);
            }

            // Для всех остальных команд - проверяем авторизацию
            if (!request.hasAuthData()) {
                response.setCommandName("error");
                response.setData("Необходима авторизация. Используйте команду 'auth' с логином и паролем.\n" +
                        "Формат: auth <логин> <пароль>\n" +
                        "Или зарегистрируйтесь: register <логин> <пароль>");
                return response;
            }

            User currentUser = authManager.authenticate(request.getLogin(), request.getPassword());

            if (currentUser == null) {
                response.setCommandName("error");
                response.setData("Ошибка авторизации. Неверный логин или пароль");
                return response;
            }

            // Проверяем, не пытается ли пользователь выполнить exit (только для админа или специальная обработка)
            if (commandName.equals("exit")) {
                response.setCommandName("exit");
                response.setData("Сервер завершает работу по запросу пользователя " + currentUser.getLogin());
                return response;
            }

            System.out.println("Пользователь " + currentUser.getLogin() + " (ID: " + currentUser.getId() +
                    ") выполняет команду: " + commandName);

            // Получаем и выполняем команду
            Command command = commandManager.executeCommand(commandName, args, request.getLabWork(), currentUser.getId());
            String result = command.execute();

            response.setCommandName("success");
            response.setData(result);

        } catch (ExitException e) {
            response.setCommandName("exit");
            response.setData(e.getMessage() != null ? e.getMessage() : "Сервер завершает работу");

        } catch (IllegalArgumentException e) {
            response.setCommandName("error");
            response.setData("Ошибка в аргументах: " + e.getMessage());

        } catch (Exception e) {
            response.setCommandName("error");
            response.setData("Ошибка выполнения команды: " + e.getMessage());
            System.err.println("Ошибка выполнения команды: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Обработка команды аутентификации
     */
    private Request handleAuth(Request request) {
        Request response = new Request();
        response.setTimestamp(System.currentTimeMillis());

        String[] args = request.getArgs();
        String login = request.getLogin();
        String password = request.getPassword();

        // Если логин и пароль переданы в аргументах, используем их
        if (args != null && args.length >= 2) {
            login = args[0];
            password = args[1];
        }

        User user = authManager.authenticate(login, password);

        if (user != null) {
            response.setCommandName("success");
            response.setData("Авторизация успешна! Добро пожаловать, " + user.getLogin() + " (ID: " + user.getId() + ")\n" +
                    "Теперь вы можете выполнять команды. Для справки используйте 'help'");
        } else {
            response.setCommandName("error");
            response.setData("Ошибка авторизации: неверный логин или пароль");
        }
        return response;
    }

    /**
     * Обработка команды регистрации
     */
    private Request handleRegister(Request request) {
        Request response = new Request();
        response.setTimestamp(System.currentTimeMillis());

        String[] args = request.getArgs();
        String login = request.getLogin();
        String password = request.getPassword();

        // Если логин и пароль переданы в аргументах, используем их
        if (args != null && args.length >= 2) {
            login = args[0];
            password = args[1];
        }

        if (login.length() < 3) {
            response.setCommandName("error");
            response.setData("Ошибка: логин должен содержать не менее 3 символов");
            return response;
        }

        if (password.length() < 4) {
            response.setCommandName("error");
            response.setData("Ошибка: пароль должен содержать не менее 4 символов");
            return response;
        }

        // Пытаемся зарегистрировать
        boolean registered = authManager.register(login, password);

        if (registered) {
            response.setCommandName("success");
            response.setData("Регистрация успешна! Теперь вы можете авторизоваться");
        } else {
            response.setCommandName("error");
            response.setData("Ошибка регистрации. Возможно, пользователь с таким логином уже существует");
        }

        return response;
    }

    /**
     * Остановка сервера
     */
    public void stop() {
        isRunning = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        System.out.println("Сервер остановлен");
    }
}
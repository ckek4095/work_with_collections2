package org.example.managers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.Arrays;


import org.example.LocalDateAdapter;
import org.example.Request;
import org.example.User;
import org.example.commands.Command;
import org.example.db.DatabaseManager;
import org.example.exceptions.ExitException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Класс Runner. Отвечает за последовательный запуск команд,
 * получаемых через DatagramSocket
 */
public class Runner {

    private CommandManager commandManager;
    private final DatagramSocket socket;
    private byte[] receiveBuffer;
    private final Gson gson;
    private static final int BUFFER_SIZE = 65536;
    private static final int SOCKET_TIMEOUT = 60000;
    private boolean isRunning;
    private AuthManager authManager;

    public Runner(CommandManager commandManager, DatagramSocket socket, DatabaseManager databaseManager) {
        this.commandManager = commandManager;
        this.socket = socket;
        this.receiveBuffer = new byte[BUFFER_SIZE];
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
    }

    public void run() throws IOException {
        System.out.println("Сервер запущен и ожидает команды...");

        while (isRunning) {
            try {
                // Очищаем буфер
                Arrays.fill(receiveBuffer, (byte) 0);

                // Создаем пакет для приема данных
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                // Принимаем пакет
                socket.receive(receivePacket);

                // Сохраняем информацию о клиенте
                java.net.InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                // Извлекаем и парсим запрос
                int dataLength = receivePacket.getLength();
                String jsonData = new String(receiveBuffer, 0, dataLength, "UTF-8");

                Request clientRequest;
                try {
                    clientRequest = gson.fromJson(jsonData, Request.class);
                    if (clientRequest == null) {
                        sendErrorResponse(clientAddress, clientPort, "Неверный формат запроса");
                        continue;
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка парсинга JSON: " + e.getMessage());
                    sendErrorResponse(clientAddress, clientPort, "Ошибка парсинга запроса: " + e.getMessage());
                    continue;
                }

                System.out.println("Получена команда от " + clientAddress + ":" + clientPort +
                        " -> " + clientRequest.getCommandName() +
                        "; аргументы: " + Arrays.toString(clientRequest.getArgs()) +
                        "; логин: " + clientRequest.getLogin());

                // Обрабатываем запрос и получаем ответ
                Request response = processRequest(clientRequest);

                // Отправляем ответ клиенту
                sendResponse(clientAddress, clientPort, response);

                // Если команда exit, завершаем работу
                if ("exit".equalsIgnoreCase(response.getCommandName())) {
                    System.out.println("Получена команда выхода, сервер останавливается...");
                    isRunning = false;
                    break;
                }

            } catch (SocketTimeoutException e) {
                continue;
            } catch (SocketException e) {
                if (isRunning) {
                    System.out.println("Сокет был закрыт: " + e.getMessage());
                }
                return;
            } catch (IOException e) {
                System.err.println("Ошибка ввода-вывода: " + e.getMessage());
                receiveBuffer = new byte[BUFFER_SIZE];
            } catch (Exception e) {
                System.err.println("Неожиданная ошибка: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Отправляет ответ клиенту
     */
    private void sendResponse(java.net.InetAddress address, int port, Request response) throws IOException {
        String jsonResponse = gson.toJson(response);
        byte[] data = jsonResponse.getBytes("UTF-8");

        // Ограничиваем размер ответа
        if (data.length > BUFFER_SIZE) {
            System.err.println("Ответ слишком большой: " + data.length + " байт");
            response.setCommandName("error");
            response.setData("Ответ слишком большой для отправки по UDP");
            jsonResponse = gson.toJson(response);
            data = jsonResponse.getBytes("UTF-8");
        }

        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);

        System.out.println("Отправлен ответ: " + response.getCommandName() +
                " - " + (response.getData() != null ? response.getData().toString().substring(0, Math.min(100, response.getData().toString().length())) : "null"));
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
    private Request processRequest(Request request) {
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
                response.setData("Ошибка авторизации. Неверный логин или пароль.\n" +
                        "Используйте 'auth <логин> <пароль>' для входа или 'register <логин> <пароль>' для регистрации");
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

        // Если всё еще нет логина/пароля, проверяем в теле запроса
        if (login == null || password == null || login.trim().isEmpty() || password.trim().isEmpty()) {
            response.setCommandName("error");
            response.setData("Ошибка: необходимо указать логин и пароль.\n" +
                    "Формат: auth <логин> <пароль>\n" +
                    "Или передайте логин и пароль в запросе");
            return response;
        }

        User user = authManager.authenticate(login, password);

        if (user != null) {
            response.setCommandName("success");
            response.setData("Авторизация успешна! Добро пожаловать, " + user.getLogin() + " (ID: " + user.getId() + ")\n" +
                    "Теперь вы можете выполнять команды. Для справки используйте 'help'");
        } else {
            response.setCommandName("error");
            response.setData("Ошибка авторизации: неверный логин или пароль.\n" +
                    "Если у вас нет аккаунта, используйте 'register <логин> <пароль>'");
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

        // Валидация
        if (login == null || password == null || login.trim().isEmpty() || password.trim().isEmpty()) {
            response.setCommandName("error");
            response.setData("Ошибка: необходимо указать логин и пароль.\n" +
                    "Формат: register <логин>\n" +
                    "Логин и пароль не могут быть пустыми");
            return response;
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
            response.setData("Регистрация успешна! Теперь вы можете авторизоваться с помощью команды 'auth " + login + " <пароль>'");
        } else {
            response.setCommandName("error");
            response.setData("Ошибка регистрации. Возможно, пользователь с таким логином уже существует.\n" +
                    "Попробуйте другой логин или используйте 'auth' для входа");
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

    /**
     * Проверка, работает ли сервер
     */
    public boolean isRunning() {
        return isRunning;
    }
}
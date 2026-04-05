package org.example.managers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.example.comands.Command;
import org.example.elems.LabWork;
import org.example.exceptions.ExitException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Класс Runner. Отвечает за последовательный запуск команд,
 * получаемых через DatagramSocket
 */
public class Runner {

    private CommandManager commandManager;
    private DatagramSocket socket;
    private byte[] receiveBuffer;
    private Gson gson;
    private static final int BUFFER_SIZE = 65536;
    private static final int SOCKET_TIMEOUT = 60000;
    private boolean isRunning;

    public Runner(CommandManager commandManager, DatagramSocket socket) {
        this.commandManager = commandManager;
        this.socket = socket;
        this.receiveBuffer = new byte[BUFFER_SIZE];
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .create();
        this.isRunning = true;
        
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
                Request clientRequest = gson.fromJson(jsonData, Request.class);
                
                System.out.println("Получена команда от " + clientAddress + ":" + clientPort + 
                                 " -> " + clientRequest.getCommandName() + "; аргументы:" + clientRequest.getArgs());
                
                // Обрабатываем запрос и получаем ответ
                Request response = processRequest(clientRequest);
                
                // Отправляем ответ клиенту
                sendResponse(clientAddress, clientPort, response);
                
                // Если команда exit, завершаем работу
                if (response.getCommandName().equals("exit")) {
                    System.out.println("Получена команда выхода, сервер останавливается...");
                    isRunning = false;
                    break;
                }
                
            } catch (SocketTimeoutException e) {
                // Таймаут - просто продолжаем ждать
                System.out.print(".");
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
        
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
        
        System.out.println("Отправлен ответ: " + response.getCommandName() + 
                         " - " + response.getData());
    }

    /**
     * Обрабатывает запрос от клиента
     */
    private Request processRequest(Request request) {
        Request response = new Request();
        response.setSessionId(request.getSessionId());
        response.setTimestamp(System.currentTimeMillis());
        LabWork labWork = request.getLabWork();
        
        try {
            String commandName = request.getCommandName();
            String[] args = request.getArgs();
            
            if (commandName == null || commandName.isEmpty()) {
                response.setCommandName("error");
                response.setData("Команда не может быть пустой");
                return response;
            }
            
            // ультра-сомнительно
            // // Проверка на exit
            // if (commandName.equalsIgnoreCase("exit")) {
            //     response.setCommandName("exit");
            //     response.setData("Сервер завершает работу");
            //     return response;
            // }
            
            // Получаем и выполняем команду
            Command command = commandManager.executeCommand(commandName, args, labWork);

            // Выполняем команду
            String result = command.execute();

            // вот тут усраться и сделать обработку add и прочих
            
            // Формируем успешный ответ
            response.setCommandName("success");
            response.setData(result);
            
        } catch (ExitException e) {
            response.setCommandName("exit");
            response.setData("Получена команда выхода");
            
        } catch (Exception e) {
            response.setCommandName("error");
            response.setData(e.getMessage());
            System.err.println("Ошибка выполнения команды: " + e.getMessage());
        }
        
        return response;
    }

    public void stop() {
        isRunning = false;
    }
}
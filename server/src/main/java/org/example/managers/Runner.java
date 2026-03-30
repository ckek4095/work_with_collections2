package org.example.managers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.example.comands.Command;
import org.example.exceptions.ExitException;

/**
 * Класс Runner. Отвечает за последовательный запуск команд,
 * получаемых через DatagramSocket
 */
public class Runner {

    private CommandManager commandManager;
    private DatagramSocket socket;
    private byte[] receiveBuffer;
    private static final int BUFFER_SIZE = 65536;
    private static final int SOCKET_TIMEOUT = 60000; // 60 секунд таймаут

    public Runner(CommandManager commandManager, DatagramSocket socket) {
        this.commandManager = commandManager;
        this.socket = socket;
        this.receiveBuffer = new byte[BUFFER_SIZE];
        
        try {
            // Устанавливаем таймаут для сокета, чтобы не блокироваться вечно
            this.socket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (SocketException e) {
            System.err.println("Не удалось установить таймаут для сокета: " + e.getMessage());
        }
    }

    public void run() throws IOException {

        receiveBuffer = new byte[BUFFER_SIZE];
        System.out.println("Сервер запущен и ожидает команды...");
        
        while (true) {
            try {
                // Создаем пакет для приема данных
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                
                // Принимаем пакет (блокирующий вызов, но с таймаутом)
                socket.receive(receivePacket);
                
                // Сохраняем информацию о клиенте для ответа
                java.net.InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                
                // Извлекаем команду из пакета
                String commandLine = new String(receivePacket.getData(), 0, receivePacket.getLength());
                commandLine = commandLine.trim();
                
                if (commandLine.isEmpty()) {
                    sendResponse(clientAddress, clientPort, "empty_command", 
                                "Команда не может быть пустой");
                    continue;
                }
                
                System.out.println("Получена команда от " + clientAddress + ":" + clientPort + 
                                 " -> " + commandLine);
                
                try {
                    // Разделяем команду и аргументы
                    String[] parts = commandLine.split("\\s+", 2);
                    String commandName = parts[0];
                    String args = parts.length > 1 ? parts[1] : "";
                    
                    // Выполняем команду
                    Command command = commandManager.executeCommand(commandName);
                    
                    // Если команда требует аргументов, передаем их
                    // if (command instanceof org.example.comands.ArgumentCommand) {
                    //     ((org.example.comands.ArgumentCommand) command).setArgs(args);
                    // }
                    
                    // Выполняем команду и получаем результат
                    String result = command.execute();
                    
                    // Отправляем результат обратно клиенту
                    sendResponse(clientAddress, clientPort, "success", result);
                    
                } catch (ExitException e) {
                    // Команда выхода
                    sendResponse(clientAddress, clientPort, "exit", "Сервер завершает работу");
                    System.out.println("Получена команда выхода, сервер останавливается...");
                    return;
                    
                } catch (Exception e) {
                    // Ошибка выполнения команды
                    sendResponse(clientAddress, clientPort, "error", e.getMessage());
                    System.err.println("Ошибка выполнения команды: " + e.getMessage());
                }
                
                // Очищаем буфер для следующего пакета
                receiveBuffer = new byte[BUFFER_SIZE];
                
            } catch (SocketTimeoutException e) {
                // Таймаут - просто продолжаем ждать
                System.out.println("Ожидание команд... (таймаут " + SOCKET_TIMEOUT/1000 + " сек)");
                continue;
                
            } catch (SocketException e) {
                System.out.println("Сокет был закрыт: " + e.getMessage());
                return;
                
            } catch (IOException e) {
                System.err.println("Ошибка ввода-вывода: " + e.getMessage());
                // Продолжаем работу, не завершаем сервер
                receiveBuffer = new byte[BUFFER_SIZE];
            }
        }
    }
    
    /**
     * Отправляет ответ клиенту
     */
    private void sendResponse(java.net.InetAddress address, int port, String status, String message) {
        try {
            // Формируем ответ в формате: статус:сообщение
            String response = status + ":" + message;
            byte[] data = response.getBytes();
            
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
            
            System.out.println("Отправлен ответ клиенту " + address + ":" + port + 
                             " [" + status + "]");
            
        } catch (IOException e) {
            System.err.println("Не удалось отправить ответ клиенту: " + e.getMessage());
        }
    }
}
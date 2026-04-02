package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainClient {

    public static void main(String[] args) {
        System.out.println("Клиент запущен. Введите команды (для выхода введите 'exit'):");
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            Sender sender = new Sender("localhost", 8080)) {
            
            while (true) {
                System.out.print(">>> ");
                String message = br.readLine();
                
                if (message == null || message.equalsIgnoreCase("exit")) {
                    System.out.println("Завершение работы клиента...");
                    break;
                }
                
                String[] parts = message.split("\\s+", 2);
                String commandName = parts[0];
                String[] arguments = parts.length > 1 ? parts[1].split(" ") : new String[0];
                
                Request request = new Request(commandName, arguments);
                request.setUsername(System.getProperty("user.name"));
                
                Request response = sender.sendAndReceive(request);
                
                handleResponse(response);
            }
            
        } catch (UnknownHostException e) {
            System.err.println("Неизвестный хост: " + e.getMessage());
        } catch (SocketException e) {
            System.err.println("Ошибка сокета: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
        }
        
        System.out.println("Клиент завершил работу");
    }

    private static void handleResponse(Request response) {
        String commandName = response.getCommandName();
        
        switch (commandName) {
            case "success":
                System.out.println("✓ " + response.getData());
                break;
            case "error":
                System.err.println("✗ Ошибка: " + response.getData());
                break;
            case "exit":
                System.out.println("Сервер завершает работу: " + response.getData());
                break;
            case "timeout":
                System.err.println("⏱ Таймаут: " + response.getData());
                break;
            default:
                System.out.println("Ответ: " + response.getData());
        }
    }
}
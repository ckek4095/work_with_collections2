package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import org.example.elem.HelperInputLab;
import org.example.elem.LabWork;
import org.example.elem.UniqueUUIDGenerator;

public class MainClient {

    public static void main(String[] args) {
        UniqueUUIDGenerator uuids = new UniqueUUIDGenerator();
        HelperInputLab helper = new HelperInputLab(uuids.getExistingIds());
        System.out.println("Клиент запущен. Введите команды (для выхода введите 'exit'):");
        LabWork labWork = new LabWork();
        
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
                Request response = new Request();

                if ((List.of("add", "add_if_max", "add_if_min").contains(commandName)) && (arguments.length == 0) ) {
                    try {
                        labWork = helper.inputLab();
                    } catch (Exception e) {
                        System.err.println("Произошла ошибка во время ввода данных: " + e.getMessage());
                    }
                } else {
                    Request request = new Request(commandName, arguments, labWork);
                    request.setUsername(System.getProperty("user.name"));
                    response = sender.sendAndReceive(request);
                }
                
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
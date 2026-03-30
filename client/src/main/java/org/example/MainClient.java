package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainClient {

    public static void main(String[] args) {
        System.out.println("Клиент запущен. Введите команды (для выхода введите 'exit'):");
        
        // Используем try-with-resources для автоматического закрытия ресурсов
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
             Sender sender = new Sender("localhost", 8080)) {
            
            while (true) {
                System.out.print("> ");
                String message = br.readLine();
                
                // Проверка на exit
                if (message == null || message.equalsIgnoreCase("exit")) {
                    System.out.println("Завершение работы клиента...");
                    break;
                }
                
                // Отправка сообщения и получение ответа
                String response = sender.sendAndReceive(message);
                
                if (response != null) {
                    System.out.println("Ответ: " + response);
                } else {
                    System.out.println("Ответ не получен (таймаут)");
                }
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
}
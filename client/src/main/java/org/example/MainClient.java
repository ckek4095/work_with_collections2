package org.example;

import org.example.utility.HelperInputLab;
import org.example.utility.UniqueUUIDGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class MainClient {

    public static void main(String[] args) {
        System.out.println("Клиент запущен. Введите команды (для выхода введите 'exit'):");

        UniqueUUIDGenerator uuids = new UniqueUUIDGenerator();
        HelperInputLab helper = new HelperInputLab(uuids.getExistingIds());
        ResponseHandler responseHandler = new ResponseHandler();

        try (ClientService clientService = new ClientService("localhost", 60000, helper);
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            while (clientService.isRunning()) {
                System.out.print(">>> ");
                String input = br.readLine();

                if (input == null || input.equalsIgnoreCase("exit")) {
                    System.out.println("Завершение работы клиента...");
                    clientService.shutdown();
                    break;
                }

                Request response = clientService.executeCommand(input);
                responseHandler.handleResponse(response);
            }

        } catch (UnknownHostException e) {
            System.err.println("Неизвестный хост: " + e.getMessage());
        } catch (SocketException e) {
            System.err.println("Ошибка сокета: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Неожиданная ошибка: " + e.getMessage());
        }

        System.out.println("Клиент завершил работу");
    }
}
package org.example;

import org.example.utility.HelperInputLab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class MainClient {

    public static void main(String[] args) {

        HelperInputLab helper = new HelperInputLab(552);
        ResponseHandler responseHandler = new ResponseHandler();

        try (ClientService clientService = new ClientService("localhost", 60000, helper);
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            System.out.println("Клиент запущен. Зарегистрируйтесь или войдите пж \n(для выхода введите 'exit'\nдля регистрации введите 'register [имя]'\nдля входа введите 'auth [имя]'):");


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
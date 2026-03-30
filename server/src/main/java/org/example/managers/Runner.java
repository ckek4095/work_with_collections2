package org.example.managers;

import org.example.comands.Command;
import org.example.exceptions.ExitException;

import java.io.BufferedReader;
import java.io.IOException;
/**
 * Класс Runner. Отвечает за последовательный запуск команд
 */
public class Runner {

    CommandManager commandManager;
    private BufferedReader br;

    public Runner(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public void run() throws IOException {
        while (true) {
            System.out.print(">>> ");
            String name = InputBR.br.readLine();
            try {
                Command command = commandManager.executeCommand(name);
                command.execute();
            } catch (ExitException e) {
                return;
            } catch (Exception e) {
                System.out.println(">>> " + e.getMessage());
            }
        }
    }
}


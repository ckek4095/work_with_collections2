package org.example.commands.implemented;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.example.commands.Command;
import org.example.models.LabWork;
import org.example.managers.CollectionManager;
import org.example.managers.CommandManager;

/**
 * Команда 'execute_script'. Считывает и выполняет скрипт из указанного файла.
 */

public class ExecuteScript implements Command {

    CollectionManager collectionManager;
    CommandManager commandManager;
    String[] args;
    List<String> history;


    public ExecuteScript(CollectionManager collectionManager, CommandManager commandManager, String[] args, List<String> history) {
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
        this.args = args;
        this.history = history;
    }

    public String execute() throws IOException {
        
        System.out.println("Начинается выполнение скрипта из файла: \n");

        String result = "";

        try (BufferedReader reader = new BufferedReader(new StringReader(args[0]))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                String[] parts = line.split("\\s+", 2);
                String commandName = parts[0];
                String[] args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];


                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }

                System.out.println("\n[Строка " + lineNumber + "] Выполняется: " + line);

                // Проверка на рекурсию (вызов самого себя)
                if (line.trim().startsWith("execute_script")) {
                    System.err.println("Обнаружен рекурсивный вызов execute_script. Пропускаем😁");
                    continue;
                }

                try {
                    history.add(commandName);
                    result += commandManager.executeCommand(commandName, args, new LabWork()).execute() + "\n";
                } catch (Exception e) {
                    System.err.println("Ошибка при выполнении команды '" + line + "': " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Ошибка при чтении содержимого файла: " + e.getMessage());
        }
        return "Выполнение скрипта завершено: \n" + result;
    }
}

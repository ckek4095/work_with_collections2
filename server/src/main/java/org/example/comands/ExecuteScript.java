package org.example.comands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemException;

import org.example.managers.CollectionManager;
import org.example.managers.CommandManager;

/**
 * Команда 'execute_script'. Считывает и выполняет скрипт из указанного файла.
 */

public class ExecuteScript implements Command{

    CollectionManager collectionManager;
    CommandManager commandManager;
    private String envVar;


    public ExecuteScript(CollectionManager collectionManager, CommandManager commandManager) {
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
        this.envVar = "SCRIPT_FILE";
    }

    public String execute() throws IOException {
        String path = "/home/enotpelmen/projects/work_with_collections2/server/src/main/java/org/example/script.txt";

        if (path == null) {
            throw new FileNotFoundException("Ошибка: Переменная окружения " + envVar + " не установлена!!!");
        }

        File file = new File(path);

        if (file.exists()) {
            if (!file.canRead()) {
                throw new FileSystemException("Ошибка: Недостаточно прав на чтение файла " + path);
            } else if (!file.isFile()) {
                throw new FileNotFoundException("Ошибка: В переменной окружения указан не файл: " + file.getName());
            }
        }

        // System.out.println("Начинается выполнение скрипта из файла: " + path);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
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
                    System.out.println(commandManager.executeCommand(commandName, args).execute());
                } catch (Exception e) {
                    System.err.println("Ошибка при выполнении команды '" + line + "': " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
        return "Выполнение скрипта завершено";
    }
}

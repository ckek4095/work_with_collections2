package org.example.comands;

import org.example.managers.CollectionManager;
import org.example.managers.CommandManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemException;

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

    public void execute() throws IOException {
        String path = System.getenv(envVar);

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

        System.out.println("Начинается выполнение скрипта из файла: " + path);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

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
                    commandManager.executeCommand(line).execute();
                } catch (Exception e) {
                    System.err.println("Ошибка при выполнении команды '" + line + "': " + e.getMessage());
                }
            }

            System.out.println("\nВыполнение скрипта завершено.");

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }
}

package org.example.commands.implemented;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import org.example.commands.Command;
import org.example.managers.CollectionManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Команда 'save'. Сохраняет коллекцию в файл
 */

public class Save implements Command {

    CollectionManager collectionManager;
    Gson gson;

    public Save(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .create();
    }

    @Override
    public String execute() throws IOException {
        String path = "/home/enotpelmen/projects/work_with_collections2/server/src/main/java/org/example/file_with_data.json";
        String text = gson.toJson(collectionManager.getCollection());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            if (path == null || path.trim().isEmpty()) {
                throw new IllegalArgumentException("Переменная окружения не установлена!!!");
            }
            writer.write(text);
            writer.flush();
            return "Сохранение выполнено)";
        } catch (IOException e) {
            throw new IOException("Ошибка ввода-вывода");
        }
    }
}
package org.example.comands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.managers.CollectionManager;
import org.example.managers.LocalDateAdapter;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Команда 'save'. Сохраняет коллекцию в файл
 */

public class Save implements Command{

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
    public void execute() throws IOException {
        String path = System.getenv("LAB_DATA_PATH");
        String text = gson.toJson(collectionManager.getCollection());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            if (path == null || path.trim().isEmpty()) {
                throw new IllegalArgumentException("Переменная окружения не установлена!!!");
            }
            writer.write(text);
            writer.flush();
            System.out.println("Сохранение выполнено)");
        } catch (IOException e) {
            throw new IOException("Ошибка ввода-вывода");
        }
    }
}
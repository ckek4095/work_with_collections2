package org.example.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.example.LocalDateAdapter;
import org.example.models.LabWork;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.example.managers.CollectionManager.validateLabWork;


/**
 * Файловый менеджер, отвечает за загрузку списка объектов из json-файла
 */

public class FileManager {

    private String envVar;
    Set<LabWork> labWorks = new LinkedHashSet<>();
    public Gson gson;

    public FileManager(String envVar) {
        this.envVar = envVar;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .create();
    }

    /**
     * Читает GSON файл и возвращает LinkedHashSet из него
     * @return LinkedHashSet сет из файла
     */

    public Set<LabWork> readFromFile() throws IOException {

        String path = envVar;

        if (path == null) {
            System.out.println("Ошибка: Переменная окружения " + envVar + " не установлена!!!");
            return null;
        }

        File file = new File(path);

        if (!file.exists()) {
            System.out.println("Ошибка: Такого файла не существует! Будет создана пустая коллекция");
            return new LinkedHashSet<LabWork>();
        }

        if (file.exists()) {
            if (!file.canRead()) {
                System.out.println("Ошибка: Недостаточно прав на чтение файла " + path + ". Будет создана пустая коллекция");
                return new LinkedHashSet<LabWork>();
            } else if (!file.isFile()) {
                System.out.println("Ошибка: В переменной окружения указан не файл: " + file.getName() + ". Будет создана пустая коллекция");
                return new LinkedHashSet<LabWork>();
            }
        }

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            Type setType = new TypeToken<Set<LabWork>>() {}.getType();
            labWorks = gson.fromJson(reader, setType);

            if (labWorks == null) {
                return new LinkedHashSet<>();
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Ошибка: Файл не найден: " + path);
        } catch (IOException e) {
            throw new IOException("Ошибка: Проблема при чтении файла: " + e.getMessage(), e);
        } catch (JsonSyntaxException e) {
            throw new IOException("Ошибка синтаксиса JSON: " + e.getMessage(), e);
        } catch (JsonParseException e) {
            throw new IOException("Ошибка парсинга JSON: " + e.getMessage(), e);
        }

        validateLabWorks(labWorks);

        return labWorks;
    }

    /**
     * Сбор всех ID из загружаемого файла
     * @param labWorks элементы из файла
     * @return список ID
     */

    public Set<String> exitingIDFromFile(Set<LabWork> labWorks) {
        Set<String> existingIds = new HashSet<>();
        for (LabWork elem : labWorks) {
            existingIds.add(elem.getId());
        }
        return existingIds;
    };

    /**
     * Валидация списка LabWork объектов
     * @param labWorks массив элементов
     */

    private void validateLabWorks(Set<LabWork> labWorks) {
        Set<String> ids = new HashSet<>();

        for (LabWork lw : labWorks) {
            // Проверка уникальности ID
            if (!ids.add(lw.getId())) {
                throw new IllegalArgumentException("Найден дубликат ID: " + lw.getId());
            }

            validateLabWork(lw);
        }
    }


    public void save(Set<LabWork> labWorks) throws IOException {
        String text = gson.toJson(labWorks);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(envVar))) {
            writer.write(text);
            writer.flush();
            System.out.println("Сохранение выполнено в файл: " + envVar);
        } catch (IOException e) {
            throw new IOException("Ошибка при сохранении в файл " + envVar + ": " + e.getMessage(), e);
        }
    }
}

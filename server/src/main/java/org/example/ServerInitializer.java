package org.example;

import java.util.Set;

import org.example.ServerRuntime;
import org.example.UDPInitialization;
import org.example.managers.*;
import org.example.models.LabWork;

public class ServerInitializer {

    public ServerRuntime initialize() {
        try {
            // 1. Путь к файлу из переменной окружения
            String dataPath = System.getenv("DATA_PATH");
            if (dataPath == null || dataPath.trim().isEmpty()) {
                throw new IllegalArgumentException("DATA_PATH не установлена");
            }

            // 2. Инициализация UDP
            UDPInitialization udpInit = new UDPInitialization();

            // 3. FileManager и загрузка коллекции
            FileManager fileManager = new FileManager(dataPath);
            Set<LabWork> collection = fileManager.readFromFile();
            Set<String> existingIds = fileManager.exitingIDFromFile(collection);

            // 4. Менеджеры коллекции и команд
            CollectionManager collectionManager = new CollectionManager(collection);
            HelperInputLabManager helperManager = new HelperInputLabManager(existingIds);
            CommandManager commandManager = new CommandManager(collectionManager, helperManager);

            // 5. Создаем и возвращаем ServerRuntime
            return new ServerRuntime(
                    udpInit.getSocket(),
                    fileManager,
                    collectionManager,
                    commandManager
            );

        } catch (Exception e) {
            System.err.println("Ошибка инициализации сервера: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
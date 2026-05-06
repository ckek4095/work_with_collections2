package org.example;

import java.util.Set;

import org.example.db.DatabaseManager;
import org.example.managers.*;
import org.example.models.LabWork;

public class ServerInitializer {

    public ServerRuntime initialize() {
        try {
            // 1. Подключаемся к БД
            DatabaseManager dbManager = new DatabaseManager(
                    ServerConfig.DB_URL,
                    ServerConfig.DB_USER,
                    ServerConfig.DB_PASSWORD
            );
            dbManager.connect();

            // 2. Загружаем коллекцию из БД
            Set<LabWork> collection = dbManager.loadCollection();
            System.out.println("Загружено " + collection.size() + " элементов из БД");

            // 3. Инициализация UDP
            UDPInitialization udpInit = new UDPInitialization();

            // 4. Менеджеры коллекции и команд
            CollectionManager collectionManager = new CollectionManager(collection);
            HelperInputLabManager helperManager = new HelperInputLabManager();
            CommandManager commandManager = new CommandManager(collectionManager, helperManager, dbManager);

            // 5. Создаем и возвращаем ServerRuntime
            return new ServerRuntime(
                    udpInit.getSocket(),
                    dbManager,
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
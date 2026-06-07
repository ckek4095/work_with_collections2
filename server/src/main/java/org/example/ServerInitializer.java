package org.example;

import java.util.Set;

import org.example.db.DatabaseManager;
import org.example.managers.*;
import org.example.models.LabWork;

public class ServerInitializer {

    public ServerRuntime initialize() {
        try {
            DatabaseManager dbManager = new DatabaseManager(
                    ServerConfig.DB_URL,
                    ServerConfig.DB_USER,
                    ServerConfig.DB_PASSWORD
            );
            dbManager.connect();

            Set<LabWork> collection = dbManager.loadCollection();
            System.out.println("Загружено " + collection.size() + " элементов из БД");

            UDPInitialization udpInit = new UDPInitialization();

            CollectionManager collectionManager = new CollectionManager(collection);
            HelperInputLabManager helperManager = new HelperInputLabManager();
            CommandManager commandManager = new CommandManager(collectionManager, helperManager, dbManager);

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
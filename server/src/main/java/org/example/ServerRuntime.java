package org.example;

import java.io.IOException;
import java.net.DatagramSocket;

import org.example.db.DatabaseManager;
import org.example.managers.CollectionManager;
import org.example.managers.CommandManager;
import org.example.managers.Runner;

public class ServerRuntime {
    private final DatagramSocket socket;
    private final DatabaseManager dbManager;
    private final CollectionManager collectionManager;
    private final CommandManager commandManager;
    private Runner runner;

    public ServerRuntime(DatagramSocket socket, DatabaseManager dbManager,
                         CollectionManager collectionManager, CommandManager commandManager) {
        this.socket = socket;
        this.dbManager = dbManager;
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
    }

    public void start() throws IOException {
        ShutdownHandler shutdownHandler = new ShutdownHandler(
                dbManager, collectionManager, socket
        );
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHandler));

        // Создаем и запускаем Runner
        runner = new Runner(commandManager, socket, dbManager);
        runner.run();
    }

    public void stop() {
        if (runner != null) {
            runner.stop();
        }
        if (dbManager != null) {
            dbManager.disconnect();
        }
    }
}
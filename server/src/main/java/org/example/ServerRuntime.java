package org.example;

import java.io.IOException;
import java.net.DatagramSocket;

import org.example.ShutdownHandler;
import org.example.managers.CollectionManager;
import org.example.managers.CommandManager;
import org.example.managers.FileManager;
import org.example.managers.Runner;

public class ServerRuntime {
    private final DatagramSocket socket;
    private final FileManager fileManager;
    private final CollectionManager collectionManager;
    private final CommandManager commandManager;
    private Runner runner;

    public ServerRuntime(DatagramSocket socket, FileManager fileManager,
                         CollectionManager collectionManager, CommandManager commandManager) {
        this.socket = socket;
        this.fileManager = fileManager;
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
    }

    public void start() throws IOException {
        // Регистрируем shutdown hook
        ShutdownHandler shutdownHandler = new ShutdownHandler(
                fileManager, collectionManager, socket
        );
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHandler));

        // Создаем и запускаем Runner
        runner = new Runner(commandManager, socket);
        runner.run();
    }

    public void stop() {
        if (runner != null) {
            runner.stop();
        }
    }
}
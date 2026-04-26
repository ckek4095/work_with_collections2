package org.example;

import java.net.DatagramSocket;

import org.example.managers.CollectionManager;
import org.example.managers.FileManager;

public class ShutdownHandler implements Runnable {
    private final FileManager fileManager;
    private final CollectionManager collectionManager;
    private final DatagramSocket socket;

    public ShutdownHandler(FileManager fileManager, CollectionManager collectionManager,
                           DatagramSocket socket) {
        this.fileManager = fileManager;
        this.collectionManager = collectionManager;
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Получен сигнал завершения, сохраняем коллекцию...");

        try {
            fileManager.save(collectionManager.getCollection());
            System.out.println("Коллекция сохранена");
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении: " + e.getMessage());
        }

        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Сокет закрыт");
        }
    }
}
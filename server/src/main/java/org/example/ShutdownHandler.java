package org.example;

import java.net.DatagramSocket;

import org.example.db.DatabaseManager;
import org.example.managers.CollectionManager;

public class ShutdownHandler implements Runnable {
    private final DatabaseManager dbManager;
    private final CollectionManager collectionManager;
    private final DatagramSocket socket;

    public ShutdownHandler(DatabaseManager dbManager, CollectionManager collectionManager,
                           DatagramSocket socket) {
        this.dbManager = dbManager;
        this.collectionManager = collectionManager;
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Получен сигнал завершения, отключаемся от БД...");

        try {
            if (dbManager != null) {
                dbManager.disconnect();
            }
        } catch (Exception e) {
            System.err.println("Ошибка при отключении от БД: " + e.getMessage());
        }

        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Сокет закрыт");
        }
    }
}
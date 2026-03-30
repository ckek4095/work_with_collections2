package org.example;

import org.example.comands.HelperInputLab;
import org.example.elems.LabWork;
import org.example.managers.*;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Set;
// 1246840
public class MainServer {

    private static final int PORT = 8080;

    static public void main(String[] args) throws IOException {

        try {
            DatagramSocket socket = new DatagramSocket(8080);
            System.out.println("UDP сервер запущен...");

            System.out.println("Сервер запущен на порту " + PORT);

            String envPath = "LAB_DATA_PATH";
            FileManager fileManager = new FileManager(envPath);
            Set<LabWork> collection = fileManager.readFromFile();
            Set<String> startExitingID = fileManager.exitingIDFromFile(collection);
            CollectionManager colManager = new CollectionManager(collection);
            HelperInputLab helper = new HelperInputLab(startExitingID);
            CommandManager commandManager = new CommandManager(colManager, helper);
            Runner runner = new Runner(commandManager);
            runner.run();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                commandManager.executeCommand("save");
            }));

            byte[] buffer = new byte[1024];



        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
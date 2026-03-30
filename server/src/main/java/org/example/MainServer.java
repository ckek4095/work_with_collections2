package org.example;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Set;

import org.example.comands.HelperInputLab;
import org.example.elems.LabWork;
import org.example.managers.CollectionManager;
import org.example.managers.CommandManager;
import org.example.managers.FileManager;
import org.example.managers.Runner;
// 1246840

// TODO rewite commands for new interfase
// TODO write class for sends to server/client like object, not string
/* Команды и их аргументы должны представлять из себя объекты классов. 
Недопустим обмен "простыми" строками. 
Так, для команды add или её аналога необходимо 
сформировать объект, содержащий тип команды и 
объект, который должен храниться в вашей 
коллекции.*/
// TODO Сетевые каналы должны использоваться в неблокирующем режиме, Сервер должен работать в однопоточном режиме
// TODO delete command save for client
// TODO made logs with "Logback"

public class MainServer {

    private static final int PORT = 8080;

    static public void main(String[] args) throws IOException {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(8080);
            System.out.println("UDP сервер запущен...");

            System.out.println("Сервер запущен на порту " + PORT);

            String envPath = "/home/enotpelmen/work_with_collections2/server/src/main/java/org/example/file_with_data.json";
            FileManager fileManager = new FileManager(envPath);
            Set<LabWork> collection = fileManager.readFromFile();
            Set<String> startExitingID = fileManager.exitingIDFromFile(collection);
            CollectionManager colManager = new CollectionManager(collection);
            HelperInputLab helper = new HelperInputLab(startExitingID);
            CommandManager commandManager = new CommandManager(colManager, helper);
            Runner runner = new Runner(commandManager, socket);
            runner.run();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                commandManager.executeCommand("save");
            }));

            byte[] buffer = new byte[1024];



        } catch (SocketException e) {
            System.err.println("Ошибка при создании сокета: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Сокет закрыт");
            }
        }
    }
}
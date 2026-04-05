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

// TODO Сетевые каналы должны использоваться в неблокирующем режиме, Сервер должен работать в однопоточном режиме
// TODO delete command save for client
// TODO made logs with "Logback"

public class MainServer {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        DatagramSocket socket = null;
        Runner runner = null;
        
        try {
            // Создаем сокет
            socket = new DatagramSocket(PORT);
            final DatagramSocket finalSocket = socket; // Делаем final для использования в лямбде
            System.out.println("UDP сервер запущен на порту " + PORT);
            
            // Инициализация менеджеров
            String envPath = "C:\\Users\\user\\IdeaProjects\\work_with_collections2\\server\\src\\main\\java\\org\\example\\file_with_data.json";
            FileManager fileManager = new FileManager(envPath);
            
            // Загрузка коллекции из файла
            Set<LabWork> collection = fileManager.readFromFile();
            Set<String> startExitingID = fileManager.exitingIDFromFile(collection);
            
            // Создание менеджеров
            CollectionManager colManager = new CollectionManager(collection);
            HelperInputLab helper = new HelperInputLab(startExitingID);
            CommandManager commandManager = new CommandManager(colManager, helper);
            
            // Создание и запуск Runner
            runner = new Runner(commandManager, socket);
            final Runner finalRunner = runner; // Делаем final для использования в лямбде
            
            // Добавляем shutdown hook для сохранения коллекции при завершении
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Получен сигнал завершения, сохраняем коллекцию...");
                try {
                    // Выполняем сохранение коллекции
                    commandManager.executeCommand("save", null, new LabWork());
                    System.out.println("Коллекция сохранена успешно");
                } catch (Exception e) {
                    System.err.println("Ошибка при сохранении коллекции: " + e.getMessage());
                } finally {
                    // Используем final переменные
                    if (finalRunner != null) {
                        finalRunner.stop();
                    }
                    if (finalSocket != null && !finalSocket.isClosed()) {
                        finalSocket.close();
                        System.out.println("Сокет закрыт");
                    }
                }
            }));
            
            // Запускаем основной цикл обработки
            runner.run();
            
        } catch (SocketException e) {
            System.err.println("Ошибка при создании сокета: " + e.getMessage());
            System.err.println("Возможно, порт " + PORT + " уже используется");
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Закрываем ресурсы
            if (runner != null) {
                runner.stop();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Сокет сервера закрыт");
            }
        }
        
        System.out.println("Сервер завершил работу");
    }
}
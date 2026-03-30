package org.example.comands;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Команда ???????????????
 */

public class SaveHistory implements Command {

    List<String> history;

    public SaveHistory(List<String> history) {
        this.history = history;
    }

    @Override
    public void execute() throws IOException, InterruptedException {
        Random random = new Random();

        System.out.println("\u001B[31mХА!!!! Ты думал это будет сохранение истории команд???\nНЕТ! Это сохранение истории браузера с твоего пк и слив данных ФСБ!!!!!");

        for (int i = 1; i <= 99; i++) {
            System.out.print("\rСлито " + i + "% данных");
            Thread.sleep(100 + random.nextInt(120)); // рандомная пауза 20-90ms
        }

        // Медленно выводим 10 точек
        System.out.print("\rСлито 99% данных");
        for (int j = 0; j < 10; j++) {
            Thread.sleep(400 + random.nextInt(300)); // рандом 400-700ms
            System.out.print(".");
        }
        System.out.println("Все данные слиты");
        System.out.println("МУХАХАХААХАХА!!!! ИЩИ СЕБЯ В РАССЫЛКЕ МЕССЕНДЖЕРА MAX\u001B[0m");
        String path = "C:/Users/user/IdeaProjects/work_with_collections/src/main/java/org/example/history.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(history.toString());
            writer.flush();
            System.out.println("))))");
        } catch (IOException e) {
            throw new IOException("Ошибка ввода-вывода");
        }
    }
}

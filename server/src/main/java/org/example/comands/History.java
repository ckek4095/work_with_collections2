package org.example.comands;

import java.io.IOException;
import java.util.List;

/**
 * Команда 'history'. Выводит последние 12 команд (без их аргументов)
 */

public class History implements Command {


    List<String> history;

    public History(List<String> history) {
        this.history = history;
    }


    @Override
    public void execute() throws IOException {
        int length = history.toArray().length;
        System.out.println("История команд: ");

        // Определяем количество элементов для вывода (не больше 12)
        int count = Math.min(length, 12);

        for (int i = 0; i < count; i++) {
            var elem = history.get(length - 1 - i);
            if (i == 0) {
                System.out.println(elem + "   <--- последняя команда");
            } else {
                System.out.println(elem);
            }
        }
        System.out.println();
    }
}
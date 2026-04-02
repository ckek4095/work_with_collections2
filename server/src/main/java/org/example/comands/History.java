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
    public String execute() throws IOException {
        int length = history.toArray().length;
        System.out.println("История команд: ");

        // Определяем количество элементов для вывода (не больше 12)
        int count = Math.min(length, 12);
        String result = "";

        for (int i = 0; i < count; i++) {
            var elem = history.get(length - 1 - i);
            if (i == 0) {
                result += elem + "   <--- последняя команда";
            } else {
                result += elem;
            }
        }
        return result;
    }
}
package org.example.comands;

import java.io.IOException;

import org.example.managers.CollectionManager;

/**
 * Команда 'remove_all_by_minimal_point'. Удаляет из коллекции все элементы, значение поля minimalPoint которого эквивалентно заданному
 */

public class RemoveAllBy implements Command {



    private CollectionManager collectionManager;
    private String[] args;

    public RemoveAllBy(CollectionManager collectionManager) {
        this(collectionManager, new String[0]);
    }

    public RemoveAllBy(CollectionManager collectionManager, String[] args) {
        this.collectionManager = collectionManager;
        this.args = args;
    }

    @Override
    public String execute() throws IOException {
        Float minimalPoint;
        // if (args.length == 0) {
        //     System.out.print(">>> Введите минимальный балл: ");
        //     String input = InputBR.br.readLine();
        //     try {
        //         minimalPoint = Float.parseFloat(input);
        //     } catch (NumberFormatException e) {
        //         throw new IOException("Ошибка: неправильный формат введенных данных");
        //     }
        // } else {
            try {
                minimalPoint = Float.parseFloat(args[0].trim());
            } catch (NumberFormatException e) {
                throw new IOException("Ошибка: неправильный формат числа в аргументе");
            }
        // }
        int sizeBefore = collectionManager.getCollection().size();
        collectionManager.getCollection().removeIf(elem -> elem.getMinimalPoint().equals(minimalPoint));
        int sizeAfter = collectionManager.getCollection().size();
        return ">>> Удалено элементов: " + (sizeBefore - sizeAfter);
    }
}
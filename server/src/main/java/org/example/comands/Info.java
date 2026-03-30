package org.example.comands;

import org.example.managers.CollectionManager;

import java.util.List;

/**
 * Команда 'info'. Выводит в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)
 */
public class Info implements Command {

    CollectionManager collectionManager;

    public Info(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void execute() {
        System.out.println("Тип коллекции: " + collectionManager.getCollection().getClass().getSimpleName());
        System.out.println("Дата инициализации: " + collectionManager.getTimeInit());
        System.out.println("Количество элементов: " + collectionManager.getCollection().toArray().length);
    }
}


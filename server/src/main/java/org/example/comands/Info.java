package org.example.comands;

import org.example.managers.CollectionManager;

/**
 * Команда 'info'. Выводит в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)
 */
public class Info implements Command {

    CollectionManager collectionManager;

    public Info(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public String execute() {
        String result = "";
        result += "Тип коллекции: " + collectionManager.getCollection().getClass().getSimpleName() + "\n";
        result += "Дата инициализации: " + collectionManager.getTimeInit() + "\n";
        result += "Количество элементов: " + collectionManager.getCollection().toArray().length + "\n";
        return result;
    }
}


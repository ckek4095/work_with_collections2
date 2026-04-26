package org.example.commands.implemented;

import org.example.Response;
import org.example.ResponseStatus;
import org.example.commands.Command;
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
        result += (collectionManager.getCollection().getClass().getSimpleName());
        result += collectionManager.getTimeInit() + "";
        result += collectionManager.getCollection().toArray().length + "";
        return result;
    }
}
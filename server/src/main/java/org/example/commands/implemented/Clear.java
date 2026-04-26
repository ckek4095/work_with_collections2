package org.example.commands.implemented;

import java.io.IOException;

import org.example.commands.Command;
import org.example.managers.CollectionManager;

/**
 * Команда 'clear'. Очищает коллекцию
 */

public class Clear implements Command {

    CollectionManager collectionManager;


    public Clear(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public String execute() throws IOException {
        collectionManager.getCollection().clear();
        return "Коллекция удачно очищена)";
    }
}

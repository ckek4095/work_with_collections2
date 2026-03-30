package org.example.comands;

import org.example.managers.CollectionManager;

import java.io.IOException;
import java.util.List;

/**
 * Команда 'clear'. Очищает коллекцию
 */

public class Clear implements Command{

    CollectionManager collectionManager;


    public Clear(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void execute() throws IOException {
        collectionManager.getCollection().clear();
        System.out.println("Коллекция удачно очищена)");
    }
}

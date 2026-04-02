package org.example.comands;

import java.io.IOException;

import org.example.managers.CollectionManager;

/**
 * Команда 'remove_by_id'. Выводит элементы, значение поля discipline которых равно заданному
 */

public class RemoveById implements Command {

    private CollectionManager collectionManager;
    private String[] args;

    public RemoveById(CollectionManager collectionManager) {
        this(collectionManager, new String[0]);
    }

    public RemoveById(CollectionManager collectionManager, String[] args) {
        this.collectionManager = collectionManager;
        this.args = args;
    }

    @Override
    public String execute() throws IOException {
        String id;
        // if (args.length == 0) {
        //     System.out.print(">>> Введите ID: ");
        //     id = InputBR.br.readLine();
        // } else {
            id = args[0].trim();
        // }
        boolean removed = collectionManager.getCollection().removeIf(elem -> elem.getId().equals(id));
        if (removed) {
            return ">>> Элемент с id " + id + " удален";
        } else {
            return ">>> Элемент с id " + id + " не найден";
        }
    }
}
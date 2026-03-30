package org.example.comands;

import org.example.elems.LabWork;
import org.example.managers.CollectionManager;

import java.io.IOException;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию.
 */
public class Add implements Command {

    protected CollectionManager collectionManager;
    protected HelperInputLab input;
    protected String[] args;

    public Add(CollectionManager collectionManager, HelperInputLab input) {
        this(collectionManager, input, new String[0]);
    }

    public Add(CollectionManager collectionManager, HelperInputLab input, String[] args) {
        this.collectionManager = collectionManager;
        this.input = input;
        this.args = args;
    }

    @Override
    public void execute() throws IOException {
        LabWork elem;
        if (args.length >= 7) {
            elem = input.inputLab(args);
        } else {
            elem = input.inputLab();
        }
        collectionManager.setLabWork(elem);
        System.out.println(">>> Элемент успешно добавлен!");
    }
}




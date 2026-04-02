package org.example.comands;

import java.io.IOException;

import org.example.elems.LabWork;
import org.example.managers.CollectionManager;

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
    public String execute() throws IOException {
        LabWork elem = new LabWork(null);
        if (args.length >= 7) {
            elem = input.inputLab(args);
        } // else {
        //     elem = input.inputLab();
        // }
        collectionManager.setLabWork(elem);
        return ">>> Элемент успешно добавлен!";
    }
}




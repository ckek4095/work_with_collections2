package org.example.commands.implemented;

import java.io.IOException;

import org.example.commands.Command;
import org.example.models.LabWork;
import org.example.managers.CollectionManager;
import org.example.managers.HelperInputLabManager;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию.
 */
public class Add implements Command {

    protected CollectionManager collectionManager;
    protected HelperInputLabManager input;
    protected String[] args;
    protected LabWork labWork;

    public Add(CollectionManager collectionManager, HelperInputLabManager input) {
        this(collectionManager, input, new String[0]);
    }

    public Add(CollectionManager collectionManager, HelperInputLabManager input, String[] args, LabWork labWork) {
        this.args = args;
        this.collectionManager = collectionManager;
        this.input = input;
        this.labWork = labWork;
    }

    public Add(CollectionManager collectionManager, HelperInputLabManager input, String[] args) {
        this.collectionManager = collectionManager;
        this.input = input;
        this.args = args;
    }

    @Override
    public String execute() throws IOException {
        LabWork elem = new LabWork();
        if (args.length >= 7) {
            elem = input.inputLab(args);
        } else elem = this.labWork;
        collectionManager.setLabWork(elem);
        return ">>> Элемент успешно добавлен!";
    }
}




package org.example.comands;

import java.io.IOException;

import org.example.elems.LabWork;
import org.example.exceptions.ElementDidntExistException;
import org.example.managers.CollectionManager;
import static org.example.managers.CollectionManager.validateLabWork;


/**
 * Команда 'update'. Обновляет значение элемента коллекции, id которого равен заданному
 */

public class UpdateById implements Command {

    private CollectionManager collectionManager;
    private HelperInputLab input;
    private String[] args;

    public UpdateById(CollectionManager collectionManager, HelperInputLab helperInput) {
        this(collectionManager, helperInput, new String[0]);
    }

    public UpdateById(CollectionManager collectionManager, HelperInputLab helperInput, String[] args) {
        this.collectionManager = collectionManager;
        this.input = helperInput;
        this.args = args;
    }

    @Override
    public String execute() throws IOException {
        String idStr = args[0];

        LabWork existing = null;
        for (LabWork elem : collectionManager.getCollection()) {
            if (elem.getId().equals(idStr)) {
                existing = elem;
                break;
            }
        }

        if (existing == null) {
            throw new ElementDidntExistException(idStr);
        }

        LabWork updated;
        if (args.length >= 8) { // id + 7 параметров
            String[] labData = new String[args.length - 1];
            System.arraycopy(args, 1, labData, 0, args.length - 1);
            updated = input.updateLabWork(existing, labData);
        } else {
            updated = input.updateLabWork(existing);
        }

        try {
            validateLabWork(updated);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        existing.update(updated);
        return ">>> Элемент с id " + idStr + " успешно обновлен!";
    }
}
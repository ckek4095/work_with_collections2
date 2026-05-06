package org.example.commands.implemented;

import java.io.IOException;
import java.sql.SQLException;

import org.example.commands.Command;
import org.example.db.DatabaseManager;
import org.example.models.LabWork;
import org.example.exceptions.ElementDidntExistException;
import org.example.managers.HelperInputLabManager;

import static org.example.managers.CollectionManager.validateLabWork;

/**
 * Команда 'update'. Обновляет значение элемента коллекции, id которого равен заданному
 */
public class UpdateById implements Command {

    private DatabaseManager databaseManager;
    private HelperInputLabManager input;
    private int userId;
    private String[] args;
    private LabWork labWork;

    public UpdateById(DatabaseManager databaseManager, HelperInputLabManager helperInput,
                      int userId, String[] args, LabWork labWork) {
        this(databaseManager, helperInput, userId, args);
        this.labWork = labWork;
    }

    public UpdateById(DatabaseManager databaseManager, HelperInputLabManager helperInput,
                      int userId, String[] args) {
        this.databaseManager = databaseManager;
        this.input = helperInput;
        this.userId = userId;
        this.args = args;
    }

    @Override
    public String execute() throws IOException, SQLException {
        long id;
        try {
            id = Long.parseLong(args[0].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new IOException("Ошибка: неправильный формат ID");
        }

        // Проверяем существование и права
        LabWork existing = null;
        for (LabWork elem : databaseManager.loadCollection()) {
            if (elem.getId() == id) {
                existing = elem;
                break;
            }
        }

        if (existing == null) {
            throw new ElementDidntExistException(String.valueOf(id));
        }

        if (existing.getOwnerId() != userId) {
            throw new SecurityException("У вас нет прав на изменение этого элемента");
        }

        LabWork updated;
        if (args.length >= 8) {
            String[] labData = new String[args.length - 1];
            System.arraycopy(args, 1, labData, 0, args.length - 1);
            updated = input.updateLabWork(existing, labData);
        } else {
            updated = input.updateLabWork(existing, labWork);
        }

        validateLabWork(updated);
        updated.setId(id); // Сохраняем исходный ID

        if (databaseManager.updateLabWork(updated, userId)) {
            return ">>> Элемент с id " + id + " успешно обновлен!";
        } else {
            return ">>> Ошибка при обновлении элемента";
        }
    }
}
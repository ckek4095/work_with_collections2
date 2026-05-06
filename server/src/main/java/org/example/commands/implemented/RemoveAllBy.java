package org.example.commands.implemented;

import java.io.IOException;
import java.sql.SQLException;

import org.example.commands.Command;
import org.example.db.DatabaseManager;
import org.example.models.LabWork;

/**
 * Команда 'remove_all_by_minimal_point'. Удаляет из коллекции все элементы,
 * значение поля minimalPoint которого эквивалентно заданному
 */
public class RemoveAllBy implements Command {

    private DatabaseManager databaseManager;
    private int userId;
    private String[] args;

    public RemoveAllBy(DatabaseManager databaseManager, int userId) {
        this(databaseManager, userId, new String[0]);
    }

    public RemoveAllBy(DatabaseManager databaseManager, int userId, String[] args) {
        this.databaseManager = databaseManager;
        this.userId = userId;
        this.args = args;
    }

    @Override
    public String execute() throws IOException, SQLException {
        Float minimalPoint;
        try {
            minimalPoint = Float.parseFloat(args[0].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new IOException("Ошибка: неправильный формат числа в аргументе");
        }

        int deletedCount = 0;
        for (LabWork elem : databaseManager.loadCollection()) {
            if (elem.getMinimalPoint().equals(minimalPoint) &&
                    elem.getOwnerId() == userId) {
                if (databaseManager.deleteLabWork(elem.getId(), userId)) {
                    deletedCount++;
                }
            }
        }

        return ">>> Удалено элементов: " + deletedCount;
    }
}
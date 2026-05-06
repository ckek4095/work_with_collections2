package org.example.commands.implemented;

import java.io.IOException;
import java.sql.SQLException;

import org.example.commands.Command;
import org.example.db.DatabaseManager;

/**
 * Команда 'remove_by_id'. Удаляет элемент по ID
 */
public class RemoveById implements Command {

    private DatabaseManager databaseManager;
    private int userId;
    private String[] args;

    public RemoveById(DatabaseManager databaseManager, int userId) {
        this(databaseManager, userId, new String[0]);
    }

    public RemoveById(DatabaseManager databaseManager, int userId, String[] args) {
        this.databaseManager = databaseManager;
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

        if (databaseManager.deleteLabWork(id, userId)) {
            return ">>> Элемент с id " + id + " удален";
        } else {
            return ">>> Элемент с id " + id + " не найден или у вас нет прав";
        }
    }
}
package org.example.commands.implemented;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import org.example.commands.Command;
import org.example.db.DatabaseManager;
import org.example.managers.CollectionManager;
import org.example.models.LabWork;

/**
 * Команда 'clear'. Очищает коллекцию
 */

public class Clear implements Command {

    DatabaseManager databaseManager;
    Integer ownerId;


    public Clear(DatabaseManager databaseManager, Integer ownerId) {
        this.databaseManager = databaseManager;
        this.ownerId = ownerId;
    }

    public String execute() throws SQLException {
        int count = databaseManager.deleteAllUserLabWorks(ownerId); // новый метод
        return "Все ваши элементы удалены - " + count + " шт" ;
    }
}

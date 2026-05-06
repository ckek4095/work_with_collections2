package org.example.commands.implemented;

import java.sql.SQLException;

import org.example.commands.Command;
import org.example.db.DatabaseManager;

/**
 * Команда 'info'. Выводит информацию о коллекции в БД
 */
public class Info implements Command {

    private DatabaseManager databaseManager;

    public Info(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public String execute() throws SQLException {
        int size = databaseManager.loadCollection().size();

        // Если нужна дополнительная статистика, можно добавить запросы к БД
        String result = "Тип коллекции: HashSet\n";
        result += "Количество элементов: " + size + "\n";

        return result;
    }
}
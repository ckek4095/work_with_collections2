package org.example.commands.implemented;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import org.example.db.DatabaseManager;
import org.example.models.LabWork;

/**
 * Команда 'filter_starts_with_name'. Выводит элементы, значение поля name которых начинается с заданной подстроки
 */
public class FilterStartsWith extends Show {

    private String[] args;
    private DatabaseManager databaseManager;

    public FilterStartsWith(DatabaseManager databaseManager, String[] args) {
        super(databaseManager);
        this.databaseManager = databaseManager;
        this.args = args;
    }

    @Override
    public String execute() throws IOException, SQLException {
        String prefix;
        if (args.length == 0) {
            throw new IllegalArgumentException("Ошибка: не указан префикс");
        }
        prefix = String.join(" ", args);

        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("Ошибка: ввод не может быть пустым");
        }

        String regex = "(?i)(?U)^" + Pattern.quote(prefix) + ".*";
        String result = "";
        boolean found = false;

        for (LabWork elem : databaseManager.loadCollection()) {
            if (elem.getName().matches(regex)) {
                result += super.showElem(elem);
                found = true;
            }
        }

        if (!found) {
            result += ">>> Элементы, начинающиеся с '" + prefix + "', не найдены";
        }
        return result;
    }
}
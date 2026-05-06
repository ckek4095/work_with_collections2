package org.example.commands.implemented;

import java.io.IOException;
import java.sql.SQLException;

import org.example.db.DatabaseManager;
import org.example.models.LabWork;

/**
 * Команда 'filter_by_discipline'. Выводит элементы, значение поля discipline которых равно заданному
 */
public class FilterByDiscipline extends Show {

    private String[] args;
    private DatabaseManager databaseManager;

    public FilterByDiscipline(DatabaseManager databaseManager, String[] args) {
        super(databaseManager);
        this.databaseManager = databaseManager;
        this.args = args;
    }

    @Override
    public String execute() throws IOException, SQLException {
        String disciplineName;
        if (args.length == 0) {
            throw new IllegalArgumentException("Ошибка: не указано название дисциплины");
        }
        disciplineName = String.join(" ", args);

        if (disciplineName == null || disciplineName.isEmpty()) {
            throw new IllegalArgumentException("Ошибка: ввод не может быть пустым");
        }

        boolean found = false;
        String result = "";
        for (LabWork elem : databaseManager.loadCollection()) {
            if (disciplineName.equals(elem.getDiscipline().getName())) {
                result += super.showElem(elem);
                found = true;
            }
        }

        if (!found) {
            return ">>> Элементы с дисциплиной '" + disciplineName + "' не найдены";
        }
        return result;
    }
}
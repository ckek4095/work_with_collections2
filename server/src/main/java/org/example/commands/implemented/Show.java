package org.example.commands.implemented;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import org.example.commands.Command;
import org.example.db.DatabaseManager;
import org.example.models.LabWork;

/**
 * Команда 'show'. Выводит коллекцию в строчном представлении.
 */

public class Show implements Command{


    DatabaseManager databaseManager;

    public Show(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public String execute() throws IOException, SQLException {
        Set<LabWork> labWorkSet = databaseManager.loadCollection();
        if (labWorkSet.isEmpty()) return "Пусто(((";
        else {
            String res = "";
            for (LabWork elem : labWorkSet) {
                res += (showElem(elem));
            }
            return res;
        }
    }

    public String showElem(LabWork elem) {
        String elemStr = "";
        elemStr += "----------------------------------------------------------\n";
        elemStr += "ID: " + elem.getId() + "\n";
        elemStr += "Имя: " + elem.getName() + "\n";
        elemStr += "Координаты: x = " + elem.getCoordinates().getX() + "; y = " + elem.getCoordinates().getY() + "\n";
        elemStr += "Дата создания: " + elem.getCreationDate() + "\n";
        elemStr += "Минимальная оценка: " + elem.getMinimalPoint() + "\n";
        elemStr += "Сложность: " + elem.getDifficulty() + "\n";
        elemStr += "Дисциплина: Наименование - \"" + elem.getDiscipline().getName() + "\"; Количество лабораторных: " + elem.getDiscipline().getLabsCount() + "\n";
        elemStr += "ID владельца: " + elem.getOwnerId() + "\n";
        return elemStr;
    }
}

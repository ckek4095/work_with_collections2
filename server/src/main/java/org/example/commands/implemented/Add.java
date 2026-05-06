package org.example.commands.implemented;

import java.io.IOException;
import java.sql.SQLException;

import org.example.commands.Command;
import org.example.db.DatabaseManager;
import org.example.models.Coordinates;
import org.example.models.Difficulty;
import org.example.models.Discipline;
import org.example.models.LabWork;
import org.example.managers.CollectionManager;
import org.example.managers.HelperInputLabManager;

import javax.xml.crypto.Data;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию.
 */
public class Add implements Command {

    protected CollectionManager collectionManager;
    protected HelperInputLabManager input;
    protected String[] args;
    protected LabWork labWork;
    protected Integer ownerId;
    protected DatabaseManager databaseManager;

    public Add(CollectionManager collectionManager, HelperInputLabManager input, String[] args, LabWork labWork, Integer ownerId, DatabaseManager databaseManager) {
        this.args = args;
        this.collectionManager = collectionManager;
        this.input = input;
        this.labWork = labWork;
        this.ownerId = ownerId;
        this.databaseManager = databaseManager;
    }

    @Override
    public String execute() throws IOException {
        try {
            if (args.length >= 7) {
                String name = args[0];
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                float minimalPoint = Float.parseFloat(args[3]);
                Difficulty difficulty = Difficulty.valueOf(args[4].toUpperCase());
                String disciplineName = args[5];
                int labsCount = Integer.parseInt(args[6]);
                Coordinates coordinates = new Coordinates(x, y);
                Discipline discipline = new Discipline(disciplineName, labsCount);

                labWork = new LabWork(name, coordinates, minimalPoint, difficulty, discipline, ownerId);
                if (labWork == null) {
                    return "Ошибка: неверные аргументы. Формат: add <name> <x> <y> <minimalPoint> <difficulty> <disciplineName> <labsCount>";
                }
            }
            boolean saved = databaseManager.saveLabWork(labWork, ownerId);
            if (!saved) {
                return "Ошибка сохранения в БД";
            }
        } catch (SQLException e) {
            return "Ошибка БД: " + e.getMessage();
        }
        return ">>> Элемент успешно добавлен!";
    }
}



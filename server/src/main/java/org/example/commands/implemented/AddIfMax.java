package org.example.commands.implemented;

import java.io.IOException;
import java.sql.SQLException;

import org.example.db.DatabaseManager;
import org.example.models.Coordinates;
import org.example.models.Difficulty;
import org.example.models.Discipline;
import org.example.models.LabWork;
import org.example.managers.CollectionManager;
import org.example.managers.HelperInputLabManager;

/**
 * Команда 'add_if_max'. Добавляет новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции (элементы сортируются по минимальной оценке)
 * @author ckek4095
 */
public class AddIfMax extends Add {

    public AddIfMax(CollectionManager collectionManager, HelperInputLabManager input, String[] args, LabWork labWork, Integer ownerId, DatabaseManager databaseManager) {
        super(collectionManager, input, args, labWork, ownerId, databaseManager);
    }

    @Override
    public String execute() throws IOException {
        LabWork elem = new LabWork();
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

        } else elem = this.labWork;
        boolean flag = true;
        for (LabWork e : collectionManager.getCollection()) {
            if (e.getMinimalPoint() > elem.getMinimalPoint()) {
                flag = false;
                break;
            }
        }
        if (flag) {
            try {
                boolean saved = databaseManager.saveLabWork(labWork, ownerId);
                if (!saved) {
                    return "Ошибка сохранения в БД";
                }
            } catch (SQLException e) {
                return "Ошибка БД: " + e.getMessage();
            }
            return ">>> Элемент успешно добавлен!!!";
        } else {
            return ">>> Упс, элемент не максимальный😭";
        }
    }
}


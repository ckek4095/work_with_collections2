package org.example.managers;

import org.example.models.Coordinates;
import org.example.models.Difficulty;
import org.example.models.Discipline;
import org.example.models.LabWork;
import org.example.utility.UniqueUUIDGenerator;

import java.io.IOException;
import java.util.Set;

/**
 * Класс HelperInputLabManager. Вспомогательный класс для интерактивного ввода элементов LabWork'и
 */

public class HelperInputLabManager {

    private UniqueUUIDGenerator generator;

    public HelperInputLabManager(Set<String> existingID) {
        this.generator = new UniqueUUIDGenerator(existingID);
    }

    // Создание из массива аргументов (для скриптов)
    public LabWork inputLab(String[] args) throws IOException {
        if (args.length < 7) {
            throw new IOException("Недостаточно данных для создания LabWork. Нужно 7 параметров, получено: " + args.length);
        }

        try {
            String id = generator.generateUniqueId();
            String name = args[0].trim();

            int x = Integer.parseInt(args[1].trim());
            Integer y = Integer.parseInt(args[2].trim());
            Coordinates coordinates = new Coordinates(x, y);

            Float minimalPoint = Float.parseFloat(args[3].trim());

            Difficulty difficulty = Difficulty.valueOf(args[4].trim().toUpperCase());

            String nameDisc = args[5].trim();
            int labsCount = Integer.parseInt(args[6].trim());
            Discipline discipline = new Discipline(nameDisc, labsCount);

            System.out.println(">>> Элемент успешно создан из переданных аргументов!");
            return new LabWork(id, name, coordinates, null, minimalPoint, difficulty, discipline);

        } catch (NumberFormatException e) {
            throw new IOException("Ошибка преобразования числа: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IOException("Некорректное значение сложности. Допустимые: EASY, NORMAL, HARD, VERY_HARD, INSANE");
        }
    }

    // Обновление из массива аргументов (для скриптов)
    public LabWork updateLabWork(LabWork elem, String[] args) throws IOException {
        if (args.length < 7) {
            throw new IOException("Недостаточно данных для обновления LabWork. Нужно 7 параметров, получено: " + args.length);
        }

        LabWork copy = new LabWork(elem);
        try {
            copy.setName(args[0].trim());

            copy.getCoordinates().setX(Integer.parseInt(args[1].trim()));
            copy.getCoordinates().setY(Integer.parseInt(args[2].trim()));

            copy.setMinimalPoint(Float.parseFloat(args[3].trim()));

            copy.setDifficulty(Difficulty.valueOf(args[4].trim().toUpperCase()));

            copy.getDiscipline().setName(args[5].trim());
            copy.getDiscipline().setLabsCount(Integer.parseInt(args[6].trim()));

            System.out.println(">>> Элемент успешно изменен из переданных аргументов!");
            return copy;

        } catch (NumberFormatException e) {
            throw new IOException("Ошибка преобразования числа: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IOException("Некорректное значение сложности. Допустимые: EASY, NORMAL, HARD, VERY_HARD, INSANE");
        }
    }

    public LabWork updateLabWork(LabWork elem, LabWork newLabWork) throws IOException {
        LabWork copy = new LabWork(elem);
        try {
            copy.setName(newLabWork.getName());

            copy.getCoordinates().setX(newLabWork.getCoordinates().getX());
            copy.getCoordinates().setY(newLabWork.getCoordinates().getY());

            copy.setMinimalPoint(newLabWork.getMinimalPoint());

            copy.setDifficulty(newLabWork.getDifficulty());

            copy.getDiscipline().setName(newLabWork.getDiscipline().getName());
            copy.getDiscipline().setLabsCount(newLabWork.getDiscipline().getLabsCount());

            System.out.println(">>> Элемент успешно изменен из переданных аргументов!");
            return copy;

        // хз наверно невохможно
        } catch (NumberFormatException e) {
            throw new IOException("Ошибка преобразования числа: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IOException("Некорректное значение сложности. Допустимые: EASY, NORMAL, HARD, VERY_HARD, INSANE");
        }
    }
}
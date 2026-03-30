package org.example.comands;

import org.example.elems.Coordinates;
import org.example.elems.Difficulty;
import org.example.elems.Discipline;
import org.example.elems.LabWork;
import org.example.managers.InputBR;
import org.example.managers.UniqueUUIDGenerator;

import java.io.IOException;
import java.util.Set;

/**
 * Класс HelperInputLab. Вспомогательный класс для интерактивного ввода элементов LabWork'и
 */

public class HelperInputLab {

    private UniqueUUIDGenerator generator;

    public HelperInputLab(Set<String> existingID) {
        this.generator = new UniqueUUIDGenerator(existingID);
    }

    // Интерактивный ввод (без аргументов)
    public LabWork inputLab() throws IOException {
        String id = generator.generateUniqueId();
        System.out.print(">>> Введите имя: ");
        String name = InputBR.br.readLine();

        System.out.print(">>> Введите координаты (каждую с новой строки):\n");
        System.out.print(">>> X (должно быть > -222): ");
        int x = Integer.parseInt(InputBR.br.readLine());

        System.out.print(">>> Y: ");
        Integer y = Integer.parseInt(InputBR.br.readLine());

        Coordinates coordinates = new Coordinates(x, y);
        System.out.print(">>> Введите минимальный балл (должен быть >0): ");
        Float minimalPoint = Float.parseFloat(InputBR.br.readLine());

        System.out.print(">>> Введите сложность (EASY, NORMAL, HARD, VERY_HARD, INSANE): ");
        Difficulty difficulty = Difficulty.valueOf(InputBR.br.readLine().toUpperCase());

        System.out.print(">>> Введите наименование дисциплины: ");
        String nameDisc = InputBR.br.readLine();

        System.out.print(">>> Введите количество лабораторных: ");
        int labsCount = Integer.parseInt(InputBR.br.readLine());
        Discipline discipline = new Discipline(nameDisc, labsCount);

        System.out.println(">>> Элемент успешно создан!");
        return new LabWork(id, name, coordinates, null, minimalPoint, difficulty, discipline);
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

    // Интерактивное обновление (без аргументов)
    public LabWork updateLabWork(LabWork elem) throws IOException {
        LabWork copy = new LabWork(elem);
        System.out.print(">>> Введите имя: ");
        copy.setName(InputBR.br.readLine());

        System.out.print(">>> Введите координаты (каждую с новой строки):\n");
        System.out.print(">>> X (должно быть > -222): ");
        copy.getCoordinates().setX(Integer.parseInt(InputBR.br.readLine()));

        System.out.print(">>> Y: ");
        copy.getCoordinates().setY(Integer.parseInt(InputBR.br.readLine()));

        System.out.print(">>> Введите минимальный балл (должен быть >0): ");
        copy.setMinimalPoint(Float.parseFloat(InputBR.br.readLine()));

        System.out.print(">>> Введите сложность (EASY, NORMAL, HARD, VERY_HARD, INSANE): ");
        copy.setDifficulty(Difficulty.valueOf(InputBR.br.readLine().toUpperCase()));

        System.out.print(">>> Введите наименование дисциплины: ");
        copy.getDiscipline().setName(InputBR.br.readLine());

        System.out.print(">>> Введите количество лабораторных: ");
        copy.getDiscipline().setLabsCount(Integer.parseInt(InputBR.br.readLine()));

        System.out.println(">>> Элемент успешно изменен!");
        return copy;
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
}
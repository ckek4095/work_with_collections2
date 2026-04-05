package org.example.elem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

/**
 * Класс HelperInputLab. Вспомогательный класс для интерактивного ввода элементов LabWork'и
 */

public class HelperInputLab {

    private UniqueUUIDGenerator generator;
    BufferedReader br;

    public HelperInputLab(Set<String> existingID) {
        this.generator = new UniqueUUIDGenerator();
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    // Интерактивный ввод (без аргументов)
    public LabWork inputLab() throws IOException {
        String id = generator.generateUniqueId();
        System.out.print(">>> Введите имя: ");
        String name = br.readLine();

        System.out.print(">>> Введите координаты (каждую с новой строки):\n");
        System.out.print(">>> X (должно быть > -222): ");
        int x = Integer.parseInt(br.readLine());

        System.out.print(">>> Y: ");
        Integer y = Integer.parseInt(br.readLine());

        Coordinates coordinates = new Coordinates(x, y);
        System.out.print(">>> Введите минимальный балл (должен быть >0): ");
        Float minimalPoint = Float.parseFloat(br.readLine());

        System.out.print(">>> Введите сложность (EASY, NORMAL, HARD, VERY_HARD, INSANE): ");
        Difficulty difficulty = Difficulty.valueOf(br.readLine().toUpperCase());

        System.out.print(">>> Введите наименование дисциплины: ");
        String nameDisc = br.readLine();

        System.out.print(">>> Введите количество лабораторных: ");
        int labsCount = Integer.parseInt(br.readLine());
        Discipline discipline = new Discipline(nameDisc, labsCount);

        System.out.println(">>> Элемент успешно создан!");
        return new LabWork(id, name, coordinates, null, minimalPoint, difficulty, discipline);
    }
}
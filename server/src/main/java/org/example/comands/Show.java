package org.example.comands;

import org.example.elems.LabWork;
import org.example.managers.CollectionManager;

import java.io.IOException;
import java.util.List;

/**
 * Команда 'show'. Выводит коллекцию в строчном представлении.
 */

public class Show implements Command{


    CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void execute() throws IOException {
        if (collectionManager.getCollection().isEmpty()) System.out.println("Коллекция пуста(((");
        else {
            for (LabWork elem : collectionManager.getCollection()) {
                showElem(elem);
            }
        }
    }

    public void showElem(LabWork elem) {
        System.out.println("----------------------------------------------------------");
        System.out.println("ID: " + elem.getId());
        System.out.println("Имя: " + elem.getName());
        System.out.println("Координаты: x = " + elem.getCoordinates().getX() + "; y = " + elem.getCoordinates().getY());
        System.out.println("Дата создания: " + elem.getCreationDate());
        System.out.println("Минимальная оценка: " + elem.getMinimalPoint());
        System.out.println("Сложность: " + elem.getDifficulty());
        System.out.println("Дисциплина: Наименование - \"" + elem.getDiscipline().getName() + "\"; Количество лабораторных: " + elem.getDiscipline().getLabsCount());
    }
}

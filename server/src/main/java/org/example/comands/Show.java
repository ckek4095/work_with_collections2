package org.example.comands;

import java.io.IOException;

import org.example.elems.LabWork;
import org.example.managers.CollectionManager;

/**
 * Команда 'show'. Выводит коллекцию в строчном представлении.
 */

public class Show implements Command{


    CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public String execute() throws IOException {
        if (collectionManager.getCollection().isEmpty()) return "Коллекция пуста(((";
        else {
            String res = "";
            for (LabWork elem : collectionManager.getCollection()) {
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
        return elemStr;
    }
}

package org.example.commands.implemented;

import java.io.IOException;

import org.example.models.LabWork;
import org.example.managers.CollectionManager;

/**
 * Команда 'filter_by_discipline'. Выводит элементы, значение поля discipline которых равно заданному
 */

public class FilterByDiscipline extends Show {

    private String[] args;

    public FilterByDiscipline(CollectionManager collectionManager) {
        this(collectionManager, new String[0]);
    }

    public FilterByDiscipline(CollectionManager collectionManager, String[] args) {
        super(collectionManager);
        this.args = args;
    }

    @Override
    public String execute() throws IOException {
        String disciplineName;
        // if (args.length == 0) {
        //     System.out.print(">>> Введите название дисциплины: ");
        //     disciplineName = InputBR.br.readLine();
        // } else {
            disciplineName = args[0].trim();
        // }
        if (disciplineName == null || disciplineName.isEmpty()) {
            throw new IllegalArgumentException("Ошибка: ввод не может быть пустым");
        }
        boolean found = false;
        String result = "";
        for (LabWork elem : collectionManager.getCollection()) {
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
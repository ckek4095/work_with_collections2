package org.example.comands;

import org.example.elems.LabWork;
import org.example.managers.CollectionManager;
import org.example.managers.InputBR;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Команда 'filter_starts_with_name'. Выводит элементы, значение поля name которых начинается с заданной подстроки
 */
public class FilterStartsWith extends Show {

    private String[] args;

    public FilterStartsWith(CollectionManager collectionManager) {
        this(collectionManager, new String[0]);
    }

    public FilterStartsWith(CollectionManager collectionManager, String[] args) {
        super(collectionManager);
        this.args = args;
    }

    @Override
    public void execute() throws IOException {
        String prefix;
        if (args.length == 0) {
            System.out.print(">>> Введите искомое название: ");
            prefix = InputBR.br.readLine();
        } else {
            prefix = args[0].trim();
        }
        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("Ошибка: ввод не может быть пустым");
        }
        String regex = "(?i)(?U)^" + Pattern.quote(prefix) + ".*";
        boolean found = false;
        for (LabWork elem : collectionManager.getCollection()) {
            if (elem.getName().matches(regex)) {
                super.showElem(elem);
                found = true;
            }
        }
        if (!found) {
            System.out.println(">>> Элементы, начинающиеся с '" + prefix + "', не найдены");
        }
    }
}


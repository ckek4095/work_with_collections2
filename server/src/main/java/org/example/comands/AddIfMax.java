package org.example.comands;

import java.io.IOException;

import org.example.elems.LabWork;
import org.example.managers.CollectionManager;

/**
 * Команда 'add_if_max'. Добавляет новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции (элементы сортируются по минимальной оценке)
 * @author ckek4095
 */
public class AddIfMax extends Add {

    public AddIfMax(CollectionManager collectionManager, HelperInputLab input) {
        this(collectionManager, input, new String[0]);
    }

    public AddIfMax(CollectionManager collectionManager, HelperInputLab input, String[] args) {
        super(collectionManager, input, args);
    }

    @Override
    public String execute() throws IOException {
        LabWork elem = new LabWork(null);
        if (args.length >= 7) {
            elem = input.inputLab(args);
        } // else {
        //     elem = input.inputLab();
        // }
        boolean flag = true;
        for (LabWork e : collectionManager.getCollection()) {
            if (e.getMinimalPoint() > elem.getMinimalPoint()) {
                flag = false;
                break;
            }
        }
        if (flag) {
            collectionManager.setLabWork(elem);
            return ">>> Элемент успешно добавлен!!!";
        } else {
            return ">>> Упс, элемент не максимальный😭";
        }
    }
}


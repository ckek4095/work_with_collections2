package org.example.comands;

import org.example.elems.LabWork;
import org.example.managers.CollectionManager;
import java.io.IOException;

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
    public void execute() throws IOException {
        LabWork elem;
        if (args.length >= 7) {
            elem = input.inputLab(args);
        } else {
            elem = input.inputLab();
        }
        boolean flag = true;
        for (LabWork e : collectionManager.getCollection()) {
            if (e.getMinimalPoint() > elem.getMinimalPoint()) {
                flag = false;
                break;
            }
        }
        if (flag) {
            collectionManager.setLabWork(elem);
            System.out.println(">>> Элемент успешно добавлен!!!");
        } else {
            System.out.println(">>> Упс, элемент не максимальный😭");
        }
    }
}


package org.example.managers;

import java.util.ArrayList;
import java.util.List;

import org.example.comands.Add;
import org.example.comands.AddIfMax;
import org.example.comands.AddIfMin;
import org.example.comands.Clear;
import org.example.comands.Command;
import org.example.comands.ExecuteScript;
import org.example.comands.Exit;
import org.example.comands.FilterByDiscipline;
import org.example.comands.FilterStartsWith;
import org.example.comands.Help;
import org.example.comands.HelperInputLab;
import org.example.comands.History;
import org.example.comands.Info;
import org.example.comands.RemoveAllBy;
import org.example.comands.RemoveById;
import org.example.comands.Show;
import org.example.comands.UpdateById;
import org.example.elems.LabWork;
import org.example.exceptions.UnknownCommandException;

/**
 * Класс CommandManager. Определяет вводимую команду и создает соответствующий объект
 */
public class CommandManager {

    private List<String> history;
    private CollectionManager colManager;
    private HelperInputLab helperInput;

    public CommandManager(CollectionManager collectionManager, HelperInputLab helper) {
        this.history = new ArrayList<>();
        this.colManager = collectionManager;
        this.helperInput = helper;
    }

    public Command executeCommand(String userInput, String[] args, LabWork labWork) {
        String[] parts = userInput.trim().split("\\s+");
        String commandName = parts[0];
        System.arraycopy(parts, 1, args, 0, parts.length - 1);

        history.add(commandName); 

        CommandType commandType = CommandType.fromString(commandName);
        if (commandType == null) {
            throw new UnknownCommandException("Команда '" + commandName + "' не найдена");
        } else return commandType.create(colManager, helperInput, args, history, labWork);

        
    }

}


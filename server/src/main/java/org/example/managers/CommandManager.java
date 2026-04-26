package org.example.managers;

import java.util.ArrayList;
import java.util.List;

import org.example.commands.Command;
import org.example.commands.CommandType;
import org.example.models.LabWork;
import org.example.exceptions.UnknownCommandException;

/**
 * Класс CommandManager. Определяет вводимую команду и создает соответствующий объект
 */
public class CommandManager {

    private List<String> history;
    private CollectionManager colManager;
    private HelperInputLabManager helperInput;

    public CommandManager(CollectionManager collectionManager, HelperInputLabManager helper) {
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


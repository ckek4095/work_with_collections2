package org.example.managers;

import org.example.comands.*;
import org.example.exceptions.UnknownCommandException;

import java.util.ArrayList;
import java.util.List;

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

    public Command executeCommand(String userInput) {
        String[] parts = userInput.trim().split("\\s+");
        String commandName = parts[0];
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, parts.length - 1);

        history.add(commandName); // можно добавить и аргументы, если нужно

        CommandType commandType = CommandType.fromString(commandName);
        if (commandType == null) {
            throw new UnknownCommandException("Команда '" + commandName + "' не найдена");
        }

        switch (commandType) {
            case ADD:
                return new Add(colManager, helperInput, args);
            case ADD_IF_MIN:
                return new AddIfMin(colManager, helperInput, args);
            case ADD_IF_MAX:
                return new AddIfMax(colManager, helperInput, args);
            case CLEAR:
                return new Clear(colManager);
            case EXECUTE_SCRIPT:
                return new ExecuteScript(colManager, new CommandManager(colManager, helperInput));
            case HELP:
                return new Help();
            case FILTER_BY_DISCIPLINE:
                return new FilterByDiscipline(colManager, args);
            case FILTER_STARTS_WITH_NAME:
                return new FilterStartsWith(colManager, args);
            case INFO:
                return new Info(colManager);
            case REMOVE_ALL_BY_MINIMAL_POINTS:
                return new RemoveAllBy(colManager, args);
            case REMOVE_BY_ID:
                return new RemoveById(colManager, args);
            case SAVE:
                return new Save(colManager);
            case SHOW:
                return new Show(colManager);
            case HISTORY:
                return new History(history);
            case EXIT:
                return new Exit();
            case UPDATE_BY_ID:
                return new UpdateById(colManager, helperInput, args);
            case SAVE_HISTORY:
                return new SaveHistory(history);
            default:
                throw new UnknownCommandException("Неизвестная команда");
        }
    }

}


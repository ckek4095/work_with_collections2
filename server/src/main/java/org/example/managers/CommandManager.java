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
        }

        switch (commandType) {
            case ADD:
                return new Add(colManager, helperInput, args, labWork);
            // todo fix
            // case ADD_IF_MIN:
            //     return new AddIfMin(colManager, helperInput, labWork);
            // case ADD_IF_MAX:
            //     return new AddIfMax(colManager, helperInput, labWork);
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
            // case SAVE:
            //     return new Save(colManager);
            case SHOW:
                return new Show(colManager);
            case HISTORY:
                return new History(history);
            case EXIT:
                return new Exit();
            case UPDATE_BY_ID:
                return new UpdateById(colManager, helperInput, args);
            // case SAVE_HISTORY:
            //     return new SaveHistory(history);
            default:
                throw new UnknownCommandException("Неизвестная команда");
        }
    }

}


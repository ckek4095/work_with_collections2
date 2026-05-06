package org.example.managers;

import java.util.ArrayList;
import java.util.List;

import org.example.commands.Command;
import org.example.commands.CommandType;
import org.example.db.DatabaseManager;
import org.example.models.LabWork;
import org.example.exceptions.UnknownCommandException;

/**
 * Класс CommandManager. Определяет вводимую команду и создает соответствующий объект
 */
public class CommandManager {

    private List<String> history;
    private CollectionManager colManager;
    private HelperInputLabManager helperInput;
    private DatabaseManager databaseManager;
    private Integer ownerId;

    public CommandManager(CollectionManager collectionManager, HelperInputLabManager helper, DatabaseManager databaseManager) {
        this.history = new ArrayList<>();
        this.colManager = collectionManager;
        this.helperInput = helper;
        this.databaseManager = databaseManager;
    }

    public Command executeCommand(String userInput, String[] args, LabWork labWork, Integer ownerId) {
        String[] parts = userInput.trim().split("\\s+");
        String commandName = parts[0];
        System.arraycopy(parts, 1, args, 0, Math.min(parts.length - 1, args.length));

        history.add(commandName);

        CommandType commandType = CommandType.fromString(commandName);
        if (commandType == null) {
            throw new UnknownCommandException("Команда '" + commandName + "' не найдена");
        }
        return commandType.create(colManager, helperInput, args, history, labWork, ownerId, databaseManager);
    }
}


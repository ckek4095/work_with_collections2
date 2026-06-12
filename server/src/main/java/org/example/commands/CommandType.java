package org.example.commands;

import java.util.List;

import org.example.commands.implemented.*;
import org.example.db.DatabaseManager;
import org.example.managers.CollectionManager;
import org.example.managers.CommandManager;
import org.example.managers.HelperInputLabManager;
import org.example.models.LabWork;

/**
 * Enum CommandType. Хранит команды и соответствующие им лямбда-выражения для создания класса команды
 */

public enum CommandType {

    ADD("add", (colManager, helperInput, args, history, labWork, ownerId, databaseManager) -> 
        new Add(colManager, helperInput, args, labWork, ownerId, databaseManager)),

    ADD_IF_MIN("add_if_min", (colManager, helperInput, args, history, labWork, ownerId, databaseManager) -> 
        new AddIfMin(colManager, helperInput, args, labWork, ownerId, databaseManager)),

    ADD_IF_MAX("add_if_max", (colManager, helperInput, args, history, labWork, ownerId, databaseManager) -> 
        new AddIfMax(colManager, helperInput, args, labWork, ownerId, databaseManager)),

    CLEAR("clear", (colManager, helperInput, args, history, labWork, ownerId, databaseManager) -> 
        new Clear(databaseManager, ownerId)),

    EXECUTE_SCRIPT("execute_script", (colManager, helperInput, args, history, labWork, ownerId, databaseManager) ->
            new ExecuteScript(new CommandManager(colManager, helperInput, databaseManager), args, history, ownerId, databaseManager)),

    HELP("help", (colManager, helperInput, args, history, labWork, ownerId, databaseManager) -> 
        new Help()),

    FILTER_BY_DISCIPLINE("filter_by_discipline", (colManager, helperInput, args, history, labWork, ownerId, databaseManager) -> 
        new FilterByDiscipline(databaseManager, args)),

    FILTER_STARTS_WITH_NAME("filter_starts_with_name", (colManager, helperInput, args, history, labWork, ownerId, databaseManager) -> 
        new FilterStartsWith(databaseManager, args)),

    INFO("info", (colManager, helperInput, args, history, labWork, ownerId, databaseManager) -> 
        new Info(databaseManager)),

    REMOVE_ALL_BY_MINIMAL_POINTS("remove_all_by_minimal_point", (colManager, helperInput, args, history, labWork, ownerId, databaseManager) ->
        new RemoveAllBy(databaseManager, ownerId, args)),

    REMOVE_BY_ID("remove_by_id", (colManager, helperInput, args, history, labWork, ownerId, databaseManager) -> 
        new RemoveById(databaseManager, ownerId, args)),

    SHOW("show", (colManager, helperInput, args, history, labWork, ownerId, databaseManager) -> 
        new Show(databaseManager)),

    HISTORY("history", (colManager, helperInput, args, history, labWork, ownerId, databaseManager) -> 
        new History(history)),

    EXIT("exit", (colManager, helperInput, args, history, labWork, ownerId, databaseManager) -> 
        new Exit()),

    UPDATE_BY_ID("update", (colManager, helperInput, args, history, labWork, ownerId, databaseManager) -> 
        new UpdateById(databaseManager, helperInput, ownerId, args, labWork));

    private final String commandName;
    private final CommandFactory factory;

    CommandType(String commandName, CommandFactory factory) {
        this.commandName = commandName;
        this.factory = factory;
    }

    public Command create(CollectionManager colManager,
                          HelperInputLabManager helperInput,
                          String[] args,
                          List<String> history,
                          LabWork labWork,
                          Integer ownerId,
                          DatabaseManager databaseManager) {
        return factory.create(colManager, helperInput, args, history, labWork, ownerId, databaseManager);
    }

    public static CommandType fromString(String name) {
        for (CommandType type : CommandType.values()) {
            if (type.commandName.equals(name)) {
                return type;
            }
        }
        return null;
    }
}




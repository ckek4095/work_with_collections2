package org.example.commands;

import java.util.List;

import org.example.commands.implemented.*;
import org.example.managers.CollectionManager;
import org.example.managers.CommandManager;
import org.example.managers.HelperInputLabManager;
import org.example.models.LabWork;

/**
 * Enum CommandType. Хранит команды и соответствующие им лямбда-выражения для создания класса команды
 */

public enum CommandType {

    ADD("add", (colManager, helperInput, args, history, labWork) -> 
        new Add(colManager, helperInput, args, labWork)),

    ADD_IF_MIN("add_if_min", (colManager, helperInput, args, history, labWork) -> 
        new AddIfMin(colManager, helperInput, args, labWork)),

    ADD_IF_MAX("add_if_max", (colManager, helperInput, args, history, labWork) -> 
        new AddIfMax(colManager, helperInput, args, labWork)),

    CLEAR("clear", (colManager, helperInput, args, history, labWork) -> 
        new Clear(colManager)),

    EXECUTE_SCRIPT("execute_script", (colManager, helperInput, args, history, labWork) -> 
        new ExecuteScript(colManager, new CommandManager(colManager, helperInput), args, history)),

    HELP("help", (colManager, helperInput, args, history, labWork) -> 
        new Help()),

    FILTER_BY_DISCIPLINE("filter_by_discipline", (colManager, helperInput, args, history, labWork) -> 
        new FilterByDiscipline(colManager, args)),

    FILTER_STARTS_WITH_NAME("filter_starts_with_name", (colManager, helperInput, args, history, labWork) -> 
        new FilterStartsWith(colManager, args)),

    INFO("info", (colManager, helperInput, args, history, labWork) -> 
        new Info(colManager)),

    REMOVE_ALL_BY_MINIMAL_POINTS("remove_all_by_minimal_points", (colManager, helperInput, args, history, labWork) -> 
        new RemoveAllBy(colManager, args)),

    REMOVE_BY_ID("remove_by_id", (colManager, helperInput, args, history, labWork) -> 
        new RemoveById(colManager, args)),

    SHOW("show", (colManager, helperInput, args, history, labWork) -> 
        new Show(colManager)),

    HISTORY("history", (colManager, helperInput, args, history, labWork) -> 
        new History(history)),

    EXIT("exit", (colManager, helperInput, args, history, labWork) -> 
        new Exit()),

    UPDATE_BY_ID("update", (colManager, helperInput, args, history, labWork) -> 
        new UpdateById(colManager, helperInput, args, labWork));

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
                          LabWork labWork) {
        return factory.create(colManager, helperInput, args, history, labWork);
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
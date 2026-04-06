package org.example.managers;

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

/**
 * Enum CommandType. Хранит команды и соответствующие им лямбда-выражения для создания класса команды
 */

interface CommandFactory {
    Command create(CollectionManager colManager, 
                   HelperInputLab helperInput, 
                   String[] args, 
                   List<String> history,
                   LabWork labWork);
}

public enum CommandType {

    ADD("add", (colManager, helperInput, args, history, labWork) -> 
        new Add(colManager, helperInput, args, labWork)),

    // ADD_IF_MIN("add_if_min", (colManager, helperInput, args, history, labWork) -> 
    //     new AddIfMin(colManager, helperInput, labWork)),

    // ADD_IF_MAX("add_if_max", (colManager, helperInput, args, history, labWork) -> 
    //     new AddIfMax(colManager, helperInput, labWork)),

    CLEAR("clear", (colManager, helperInput, args, history, labWork) -> 
        new Clear(colManager)),

    EXECUTE_SCRIPT("execute_script", (colManager, helperInput, args, history, labWork) -> 
        new ExecuteScript(colManager, new CommandManager(colManager, helperInput))),

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

    // низя юзать 
    // SAVE("save", (colManager, helperInput, args, history, labWork) -> 
    //     new Save(colManager)),

    SHOW("show", (colManager, helperInput, args, history, labWork) -> 
        new Show(colManager)),

    HISTORY("history", (colManager, helperInput, args, history, labWork) -> 
        new History(history)),

    EXIT("exit", (colManager, helperInput, args, history, labWork) -> 
        new Exit()),

    UPDATE_BY_ID("update", (colManager, helperInput, args, history, labWork) -> 
        new UpdateById(colManager, helperInput, args));

    // SAVE_HISTORY("save_history", (colManager, helperInput, args, history, labWork) -> 
    //     new SaveHistory(history));

    private final String commandName;
    private final CommandFactory factory;

    CommandType(String commandName, CommandFactory factory) {
        this.commandName = commandName;
        this.factory = factory;
    }

    public String getCommandName() {
        return commandName;
    }

    public Command create(CollectionManager colManager, 
                         HelperInputLab helperInput, 
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
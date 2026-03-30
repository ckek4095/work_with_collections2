package org.example.managers;

/**
 * Enum CommandType. Хранит команды и соответствующие им строки, которые должен ввести пользователь
 */

public enum CommandType {


    ADD("add"),
    ADD_IF_MIN("add_if_min"),
    ADD_IF_MAX("add_if_max"),
    CLEAR("clear"),
    EXECUTE_SCRIPT("execute_script"),
    HELP("help"),
    FILTER_BY_DISCIPLINE("filter_by_discipline"),
    FILTER_STARTS_WITH_NAME("filter_starts_with_name"),
    INFO("info"),
    REMOVE_ALL_BY_MINIMAL_POINTS("remove_all_by_minimal_points"),
    REMOVE_BY_ID("remove_by_id"),
    SAVE("save"),
    SHOW("show"),
    HISTORY("history"),
    EXIT("exit"),
    UPDATE_BY_ID("update"), SAVE_HISTORY("save_history");

    private final String commandName;

    CommandType(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
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
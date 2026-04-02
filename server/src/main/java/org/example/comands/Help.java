package org.example.comands;

/**
 * Команда 'help'. Выводит информацию о командах.
 * @author ckek4095
 */
public class Help implements Command {

    public String execute() {
        StringBuilder information = new StringBuilder();
        information.append("help : вывести справку по доступным командам\n");
        information.append("info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n");
        information.append("show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n");
        information.append("add {element} : добавить новый элемент в коллекцию\n");
        information.append("update id {element} : обновить значение элемента коллекции, id которого равен заданному\n");
        information.append("remove_by_id id : удалить элемент из коллекции по его id\n");
        information.append("clear : очистить коллекцию\n");
        information.append("save : сохранить коллекцию в файл\n");
        information.append("execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n");
        information.append("exit : завершить программу (без сохранения в файл)");
        information.append("add_if_max {element} : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции (элементы сортируются по минимальной оценке)\n");
        information.append("add_if_min {element} : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции (элементы сортируются по минимальной оценке)\n");
        information.append("history : вывести последние 12 команд (без их аргументов)\n");
        information.append("remove_all_by_minimal_point minimalPoint : удалить из коллекции все элементы, значение поля minimalPoint которого эквивалентно заданному\n");
        information.append("filter_by_discipline discipline : вывести элементы, значение поля discipline которых равно заданному\n");
        information.append("filter_starts_with_name name : вывести элементы, значение поля name которых начинается с заданной подстроки\n");
        return information.toString();
    }
}


package org.example;

import java.io.Serializable;
import java.util.Arrays;

import org.example.models.LabWork;

/**
 * Класс Request для обмена данными между клиентом и сервером
 */
public class Request implements Serializable {
    private String commandName = "";
    private String[] args;
    private String login;
    private String password;
    private long timestamp;
    private LabWork labWork;
    private Object data;

    public Request() {
        this.timestamp = System.currentTimeMillis();
    }

    public Request(String commandName) {
        this.commandName = commandName;
        this.timestamp = System.currentTimeMillis();
    }

    public Request(String commandName, String[] args, String login, String password) {
        this(commandName);
        this.args = args;
        this.login = login;
        this.password = password;
    }

    public Request(String commandName, String[] args, LabWork labWork, String login, String password) {
        this(commandName, args, login, password);
        this.labWork = labWork;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public LabWork getLabWork() {
        return labWork;
    }

    public void setLabWork(LabWork labWork) {
        this.labWork = labWork;
    }

    /**
     * Проверка, есть ли у запроса данные для авторизации
     */
    public boolean hasAuthData() {
        return (login != null) && (!login.trim().isEmpty());
    }

    @Override
    public String toString() {
        return "Request{" +
                "commandName='" + commandName + '\'' +
                ", args=" + Arrays.toString(args) +
                ", login='" + login + '\'' +
                ", timestamp=" + timestamp +
                ", hasLabWork=" + (labWork != null) +
                '}';
    }
}
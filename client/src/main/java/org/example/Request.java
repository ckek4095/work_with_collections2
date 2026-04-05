package org.example;

import java.io.Serializable;
import java.util.Arrays;

import org.example.elem.LabWork;

/**
 * Класс Request для обмена данными между клиентом и сервером
 */
public class Request implements Serializable {
    private String commandName = "";
    private String[] args;
    private String username;
    private long timestamp;
    private String sessionId;
    private LabWork labWork;
    private Object data; // Для дополнительных данных
    
    public Request() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public Request(String commandName) {
        this.commandName = commandName;
    }
    
    public Request(String commandName, String[] args) {
        this(commandName);
        this.args = args;
    }
    
    public Request(String commandName, String[] args, LabWork labWork) {
        this(commandName, args);
        this.labWork = labWork;
    }
    
    // Геттеры и сеттеры
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
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }

    public void setLabWork(LabWork labWork) {
        this.labWork = labWork;
    }

    public LabWork getLabWork() {
        return labWork;
    }
    
    @Override
    public String toString() {
        return "Request{" +
                "commandName='" + commandName + '\'' +
                ", args=" + Arrays.toString(args) +
                ", username='" + username + '\'' +
                ", timestamp=" + timestamp +
                ", sessionId='" + sessionId + '\'' +
                ", data=" + data +
                '}';
    }
}
package org.example;

import org.example.models.LabWork;
import java.util.Arrays;

public class CommandModel {
    private final String name;
    private final String[] arguments;
    private final LabWork labWork;
    private final String scriptContent;
    private String login;
    private String password;

    public CommandModel(String name, String[] arguments) {
        this(name, arguments, null, null);
    }

    public CommandModel(String name, String[] arguments, LabWork labWork, String scriptContent) {
        this.name = name;
        this.arguments = arguments;
        this.labWork = labWork;
        this.scriptContent = scriptContent;
        this.login = "";
        this.password = "";
    }

    // Getters
    public String getName() { return name; }
    public String[] getArguments() { return arguments; }
    public LabWork getLabWork() { return labWork; }
    public String getScriptContent() { return scriptContent; }
    public boolean hasLabWork() { return labWork != null; }
    public boolean hasScriptContent() { return scriptContent != null; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return String.format("CommandModel{name='%s', args=%s, hasLabWork=%s, login='%s'}",
                name, Arrays.toString(arguments), hasLabWork(), login);
    }
}
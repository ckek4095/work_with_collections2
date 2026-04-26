package org.example;

import org.example.models.LabWork;

public class CommandModel {
    private final String name;
    private final String[] arguments;
    private final LabWork labWork;
    private final String scriptContent;

    public CommandModel(String name, String[] arguments) {
        this(name, arguments, null, null);
    }

    public CommandModel(String name, String[] arguments, LabWork labWork) {
        this(name, arguments, labWork, null);
    }

    public CommandModel(String name, String[] arguments, String scriptContent) {
        this(name, arguments, null, scriptContent);
    }

    public CommandModel(String name, String[] arguments, LabWork labWork, String scriptContent) {
        this.name = name;
        this.arguments = arguments;
        this.labWork = labWork;
        this.scriptContent = scriptContent;
    }

    // Getters
    public String getName() { return name; }
    public String[] getArguments() { return arguments; }
    public LabWork getLabWork() { return labWork; }
    public String getScriptContent() { return scriptContent; }
    public boolean hasLabWork() { return labWork != null; }
    public boolean hasScriptContent() { return scriptContent != null; }
}
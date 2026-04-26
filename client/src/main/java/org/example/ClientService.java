package org.example;

import org.example.connect.Sender;
import org.example.utility.HelperInputLab;

import java.io.IOException;

public class ClientService implements AutoCloseable {
    private final Sender sender;
    private final CommandProcessor commandProcessor;
    private final ResponseHandler responseHandler;
    private boolean isRunning = true;

    public ClientService(String host, int port, HelperInputLab helper) throws IOException {
        this.sender = new Sender(host, port);
        this.responseHandler = new ResponseHandler();
        this.commandProcessor = new CommandProcessor(
                helper,
                sender,
                System.getProperty("user.name")
        );
    }

    public Request executeCommand(String commandLine) {
        if (!isRunning || commandLine == null || commandLine.trim().isEmpty()) {
            return null;
        }

        try {
            CommandModel command = parseCommandLine(commandLine);
            return commandProcessor.processCommand(command);
        } catch (Exception e) {
            Request errorRequest = new Request("error");
            errorRequest.setData(e.getMessage());
            return errorRequest;
        }
    }

    private CommandModel parseCommandLine(String commandLine) {
        String[] parts = commandLine.trim().split("\\s+", 2);
        String commandName = parts[0];
        String[] arguments = parts.length > 1 ? parts[1].split(" ") : new String[0];
        return new CommandModel(commandName, arguments);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void shutdown() {
        isRunning = false;
    }

    @Override
    public void close() throws Exception {
        if (sender != null) {
            sender.close();
        }
    }
}
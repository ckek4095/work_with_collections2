package org.example.gui;

import org.example.Request;
import org.example.connect.Sender;
import org.example.models.LabWork;

import java.io.IOException;

public class GuiClientService implements AutoCloseable {
    private final Sender sender;

    private String login;
    private String password;

    public GuiClientService(String host, int port) throws IOException {
        this.sender = new Sender(host, port);
    }

    public Request login(String login, String password) throws IOException {
        Request request = new Request("auth");
        request.setLogin(login);
        request.setPassword(password);
        request.setArgs(new String[]{login, password});

        Request response = sender.sendAndReceive(request);

        if (isSuccess(response)) {
            this.login = login;
            this.password = password;
        }

        return response;
    }

    public Request register(String login, String password) throws IOException {
        Request request = new Request("register");
        request.setLogin(login);
        request.setPassword(password);
        request.setArgs(new String[]{login, password});

        return sender.sendAndReceive(request);
    }

    public Request executeSimpleCommand(String commandName, String... args) throws IOException {
        Request request = new Request(commandName);
        request.setArgs(args);
        request.setLogin(login);
        request.setPassword(password);

        return sender.sendAndReceive(request);
    }

    public Request executeLabWorkCommand(String commandName, LabWork labWork, String... args) throws IOException {
        Request request = new Request(commandName);
        request.setArgs(args);
        request.setLabWork(labWork);
        request.setLogin(login);
        request.setPassword(password);

        return sender.sendAndReceive(request);
    }

    public boolean isSuccess(Request response) {
        return response != null && "success".equalsIgnoreCase(response.getCommandName());
    }

    public String getResponseText(Request response) {
        if (response == null) {
            return "Нет ответа от сервера";
        }

        Object data = response.getData();
        return data == null ? "" : data.toString();
    }

    public boolean isAuthorized() {
        return login != null && !login.isBlank()
                && password != null && !password.isBlank();
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void close() {
        sender.close();
    }
}
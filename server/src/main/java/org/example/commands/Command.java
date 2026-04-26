package org.example.commands;

import java.io.IOException;

public interface Command {
    String execute() throws IOException, InterruptedException;
}
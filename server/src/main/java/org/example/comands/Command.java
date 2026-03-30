package org.example.comands;

import java.io.IOException;

public interface Command {
    String execute() throws IOException, InterruptedException;
}

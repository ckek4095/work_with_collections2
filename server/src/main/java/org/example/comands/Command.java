package org.example.comands;

import java.io.IOException;

public interface Command {
    void execute() throws IOException, InterruptedException;
}

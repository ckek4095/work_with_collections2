package org.example.commands;

import java.io.IOException;
import java.sql.SQLException;

public interface Command {
    String execute() throws IOException, InterruptedException, SQLException;
}
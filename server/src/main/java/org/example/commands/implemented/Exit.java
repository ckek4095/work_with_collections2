package org.example.commands.implemented;

import java.io.IOException;

import org.example.commands.Command;
import org.example.exceptions.ExitException;

/**
 * Команда 'exit'. Завершает работу программы
 */

public class Exit implements Command {

    public String execute() throws IOException {
        throw new ExitException("return hihihoho");
    }
}

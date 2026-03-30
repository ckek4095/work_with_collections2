package org.example.comands;

import org.example.exceptions.ExitException;

import java.io.IOException;
import java.util.List;

/**
 * Команда 'exit'. Завершает работу программы
 */

public class Exit implements Command {

    public void execute() throws IOException {
        throw new ExitException("return hihihoho");
    }
}

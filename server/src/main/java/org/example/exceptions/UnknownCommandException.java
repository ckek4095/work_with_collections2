package org.example.exceptions;

/**
 * Ошибка отсутствия команды с таким названием
 */

public class UnknownCommandException extends RuntimeException {

    public UnknownCommandException(String message) {
        super(message);
    }
}

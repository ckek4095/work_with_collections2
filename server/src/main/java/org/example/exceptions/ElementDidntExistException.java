package org.example.exceptions;

/**
 * Ошибка отсутствия элемента с таким параметром
 */

public class ElementDidntExistException extends RuntimeException {

    public ElementDidntExistException(String arg) {
        super(arg);
    }

    public String getMessage() {
        return "элемент с id " + super.getMessage() + " не найден";
    }
}

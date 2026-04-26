package org.example;

public enum ResponseStatus {
    SUCCESS,    // Команда выполнена, есть текстовое сообщение
    ERROR,      // Ошибка
    EXIT        // Сигнал клиенту закрыться
}
package org.example.managers;

import org.example.elems.LabWork;

import java.util.*;
import java.time.LocalDateTime;

/**
 * Отвечает за работу с коллекцией и ее элементами
 */

public class CollectionManager {

    Set<LabWork> collection;
    java.time.LocalDateTime timeInit;
    HashSet<String> ids;

    public CollectionManager(Set<LabWork> collection) {
        this.collection = collection;
        this.timeInit = LocalDateTime.now();
    }

    public LocalDateTime getTimeInit() {
        return timeInit;
    }

    public Set<LabWork> getCollection() {
        return collection;
    }

    public void setLabWork(LabWork lw) {
        validateLabWork(lw);
        collection.add(lw);
    }

    /**
     * Валидация отдельного LabWork объекта
     * @param lw элемент сета
     */

    static public void validateLabWork(LabWork lw) {
        if (lw.getId() == null || lw.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("ID не может быть пустым");
        }

        if (lw.getName() == null || lw.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }

        if (lw.getCoordinates() == null) {
            throw new IllegalArgumentException("Координаты не могут отсутствовать");
        }

        if (lw.getCoordinates().getX() <= -222) {
            throw new IllegalArgumentException("Координата X не может быть <= -222");
        }

        if (lw.getCreationDate() == null) {
            throw new IllegalArgumentException("Дата создания не может отсутствовать");
        }

        if (lw.getMinimalPoint() != null && lw.getMinimalPoint() <= 0) {
            throw new IllegalArgumentException("Минимальный балл должно быть > 0");
        }

        if (lw.getDiscipline() == null) {
            throw new IllegalArgumentException("Дисциплина не может отсутствовать");
        }

        if (lw.getDiscipline().getName() == null || lw.getDiscipline().getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название дисциплины не может быть пустым");
        }
    }
}

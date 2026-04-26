package org.example.utility;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Класс для генерации уникальных uuid
 */
public class UniqueUUIDGenerator {

    private Set<String> existingIds = new HashSet<>();

    public UniqueUUIDGenerator() {
    }

    public UniqueUUIDGenerator(Set<String> existingIds) {
        this.existingIds = new HashSet<>(existingIds);  // копируем существующие
    }

    /**
     * Метод для генерации. Если генерируется используемый ключ, он его перегенерирует
     * @return уникальный ключ
     */

    public String generateUniqueId() {
        String newId;
        do {
            newId = UUID.randomUUID().toString();
        } while (existingIds.contains(newId));

        existingIds.add(newId);
        return newId;
    }
}


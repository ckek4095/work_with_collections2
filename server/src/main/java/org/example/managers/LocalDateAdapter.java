package org.example.managers;

import com.google.gson.*;


import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Вспомогательный класс LocalDateAdapter. Позволяет правильно работать со временем в JSON
 */

public class LocalDateAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
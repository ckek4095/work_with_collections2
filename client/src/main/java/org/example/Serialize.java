package org.example;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Serialize {

    private final Gson gson;

    public Serialize() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .create();
    }
    
    public String serialize(Object object) {
        return gson.toJson(object);
    }

    public byte[] serializeToBytes(Object request) {
        String json = serialize(request);
        return json.getBytes(StandardCharsets.UTF_8);
    }

    public <T> T deserialize(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public boolean isValidJson(String json) {
    try {
        JsonParser.parseString(json);
        return true;
    } catch (JsonSyntaxException e) {
        return false;
    }
    }
}


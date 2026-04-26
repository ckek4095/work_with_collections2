package org.example;

import java.io.Serializable;

public class Response implements Serializable {

    private ResponseStatus status;
    private String message;      // Текстовое сообщение
    private Object payload;      // Полезная нагрузка (List<LabWork>, InfoData и т.д.)

    public Response(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public Response(ResponseStatus status, String message, Object payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
    }

    public ResponseStatus getStatus() { return status; }
    public String getMessage() { return message; }
    public Object getPayload() { return payload; }

    public void setStatus(ResponseStatus status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setPayload(Object payload) { this.payload = payload; }
}
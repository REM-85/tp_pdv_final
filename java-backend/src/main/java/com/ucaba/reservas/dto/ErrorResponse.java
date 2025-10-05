package com.ucaba.reservas.dto;

public class ErrorResponse {

    private final String message;
    private final String correlationId;

    public ErrorResponse(String message, String correlationId) {
        this.message = message;
        this.correlationId = correlationId;
    }

    public String getMessage() {
        return message;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}

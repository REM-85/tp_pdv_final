package com.ucaba.reservas.dto;

import java.time.LocalDateTime;

public class HistoryPoint {

    private final LocalDateTime date;
    private final double value;

    public HistoryPoint(LocalDateTime date, double value) {
        this.date = date;
        this.value = value;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public double getValue() {
        return value;
    }
}

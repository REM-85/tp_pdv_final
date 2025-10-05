package com.ucaba.reservas.dto;

import java.time.LocalDate;

public class ForecastPoint {

    private final LocalDate date;
    private final double yhat;
    private final Double yhatLower;
    private final Double yhatUpper;

    public ForecastPoint(LocalDate date, double yhat, Double yhatLower, Double yhatUpper) {
        this.date = date;
        this.yhat = yhat;
        this.yhatLower = yhatLower;
        this.yhatUpper = yhatUpper;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getYhat() {
        return yhat;
    }

    public Double getYhatLower() {
        return yhatLower;
    }

    public Double getYhatUpper() {
        return yhatUpper;
    }
}

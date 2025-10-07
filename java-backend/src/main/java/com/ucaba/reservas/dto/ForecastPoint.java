package com.ucaba.reservas.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class ForecastPoint {

    private final LocalDate date;
    private final double yhat;
    private final Double yhatLower;
    private final Double yhatUpper;

    @JsonCreator
    public ForecastPoint(@JsonProperty("date") LocalDate date,
                         @JsonProperty("yhat") double yhat,
                         @JsonProperty("yhatLower") Double yhatLower,
                         @JsonProperty("yhatUpper") Double yhatUpper) {
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

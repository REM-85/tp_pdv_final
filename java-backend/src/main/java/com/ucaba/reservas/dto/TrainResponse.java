package com.ucaba.reservas.dto;

import java.util.Map;

public class TrainResponse {

    private final String seriesId;
    private final Map<String, Double> metrics;

    public TrainResponse(String seriesId, Map<String, Double> metrics) {
        this.seriesId = seriesId;
        this.metrics = Map.copyOf(metrics);
    }

    public String getSeriesId() {
        return seriesId;
    }

    public Map<String, Double> getMetrics() {
        return metrics;
    }
}

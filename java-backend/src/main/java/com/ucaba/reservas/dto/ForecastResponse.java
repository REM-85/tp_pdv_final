package com.ucaba.reservas.dto;

import java.util.List;
import java.util.Map;

public class ForecastResponse {

    private final String seriesId;
    private final Map<String, Double> metrics;
    private final List<ForecastPoint> points;

    public ForecastResponse(String seriesId, Map<String, Double> metrics, List<ForecastPoint> points) {
        this.seriesId = seriesId;
        this.metrics = Map.copyOf(metrics);
        this.points = List.copyOf(points);
    }

    public String getSeriesId() {
        return seriesId;
    }

    public Map<String, Double> getMetrics() {
        return metrics;
    }

    public List<ForecastPoint> getPoints() {
        return points;
    }
}

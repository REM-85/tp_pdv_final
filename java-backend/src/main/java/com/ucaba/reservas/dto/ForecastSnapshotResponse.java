package com.ucaba.reservas.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ForecastSnapshotResponse {

    private final Long id;
    private final String seriesId;
    private final String resourceType;
    private final Long resourceId;
    private final LocalDateTime generatedAt;
    private final int horizonDays;
    private final Map<String, Double> metrics;
    private final List<ForecastPoint> points;

    public ForecastSnapshotResponse(Long id,
                                    String seriesId,
                                    String resourceType,
                                    Long resourceId,
                                    LocalDateTime generatedAt,
                                    int horizonDays,
                                    Map<String, Double> metrics,
                                    List<ForecastPoint> points) {
        this.id = id;
        this.seriesId = seriesId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.generatedAt = generatedAt;
        this.horizonDays = horizonDays;
        this.metrics = Map.copyOf(metrics);
        this.points = List.copyOf(points);
    }

    public Long getId() {
        return id;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public int getHorizonDays() {
        return horizonDays;
    }

    public Map<String, Double> getMetrics() {
        return metrics;
    }

    public List<ForecastPoint> getPoints() {
        return points;
    }
}

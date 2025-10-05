package com.ucaba.reservas.dto;

import java.util.List;

public class HistoryResponse {

    private final String seriesId;
    private final String resourceType;
    private final Long resourceId;
    private final List<HistoryPoint> points;

    public HistoryResponse(String seriesId, String resourceType, Long resourceId, List<HistoryPoint> points) {
        this.seriesId = seriesId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.points = List.copyOf(points);
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

    public List<HistoryPoint> getPoints() {
        return points;
    }
}

package com.ucaba.reservas.dto;

import java.time.LocalDateTime;

public class AvailabilityResponse {

    private final String resourceType;
    private final Long resourceId;
    private final boolean available;
    private final LocalDateTime requestedStart;
    private final LocalDateTime requestedEnd;

    public AvailabilityResponse(String resourceType,
                                 Long resourceId,
                                 boolean available,
                                 LocalDateTime requestedStart,
                                 LocalDateTime requestedEnd) {
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.available = available;
        this.requestedStart = requestedStart;
        this.requestedEnd = requestedEnd;
    }

    public String getResourceType() {
        return resourceType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public boolean isAvailable() {
        return available;
    }

    public LocalDateTime getRequestedStart() {
        return requestedStart;
    }

    public LocalDateTime getRequestedEnd() {
        return requestedEnd;
    }
}

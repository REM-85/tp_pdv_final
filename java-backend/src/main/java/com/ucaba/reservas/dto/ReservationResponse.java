package com.ucaba.reservas.dto;

import java.time.LocalDateTime;

public class ReservationResponse {

    private final Long id;
    private final Long personId;
    private final String personName;
    private final String resourceType;
    private final Long resourceId;
    private final String resourceName;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    public ReservationResponse(Long id,
                               Long personId,
                               String personName,
                               String resourceType,
                               Long resourceId,
                               String resourceName,
                               LocalDateTime startDateTime,
                               LocalDateTime endDateTime) {
        this.id = id;
        this.personId = personId;
        this.personName = personName;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public Long getId() {
        return id;
    }

    public Long getPersonId() {
        return personId;
    }

    public String getPersonName() {
        return personName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }
}

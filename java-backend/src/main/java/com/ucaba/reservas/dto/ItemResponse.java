package com.ucaba.reservas.dto;

public class ItemResponse {

    private final Long id;
    private final String name;
    private final boolean available;

    public ItemResponse(Long id, String name, boolean available) {
        this.id = id;
        this.name = name;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return available;
    }
}

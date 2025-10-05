package com.ucaba.reservas.dto;

public class RoomResponse {

    private final Long id;
    private final String name;
    private final int capacity;

    public RoomResponse(Long id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }
}

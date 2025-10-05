package com.ucaba.reservas.dto;

import jakarta.validation.constraints.NotBlank;

public class ItemRequest {

    @NotBlank
    private String name;

    private boolean available = true;

    public ItemRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}

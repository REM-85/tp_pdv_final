package com.ucaba.reservas.dto;

public class AuthResponse {

    private final String token;
    private final long expiresIn;
    private final String fullName;
    private final String email;
    private final String role;

    public AuthResponse(String token, long expiresIn, String fullName, String email, String role) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}

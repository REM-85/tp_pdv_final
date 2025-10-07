package com.ucaba.reservas.service;

import com.ucaba.reservas.dto.AuthRequest;
import com.ucaba.reservas.dto.AuthResponse;
import com.ucaba.reservas.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(AuthRequest request);
}

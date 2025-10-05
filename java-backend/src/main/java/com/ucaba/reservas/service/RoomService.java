package com.ucaba.reservas.service;

import com.ucaba.reservas.dto.RoomRequest;
import com.ucaba.reservas.dto.RoomResponse;
import java.util.List;

public interface RoomService {
    List<RoomResponse> findAll();

    RoomResponse findById(Long id);

    RoomResponse create(RoomRequest request);

    RoomResponse update(Long id, RoomRequest request);

    void delete(Long id);
}


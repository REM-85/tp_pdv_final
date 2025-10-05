package com.ucaba.reservas.service;

import com.ucaba.reservas.dto.AvailabilityResponse;
import com.ucaba.reservas.dto.HistoryResponse;
import com.ucaba.reservas.dto.ReservationRequest;
import com.ucaba.reservas.dto.ReservationResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService {
    List<ReservationResponse> findAll();

    ReservationResponse findById(Long id);

    ReservationResponse create(ReservationRequest request);

    ReservationResponse update(Long id, ReservationRequest request);

    void delete(Long id);

    AvailabilityResponse checkAvailability(String resourceType, Long resourceId, LocalDateTime start, LocalDateTime end);

    HistoryResponse buildHistory(String resourceType, Long resourceId, LocalDate start, LocalDate end);
}


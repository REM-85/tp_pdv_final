package com.ucaba.reservas.repository;

import com.ucaba.reservas.model.Reservation;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByRoomIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            Long roomId, LocalDateTime end, LocalDateTime start);

    List<Reservation> findByItemIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            Long itemId, LocalDateTime end, LocalDateTime start);

    List<Reservation> findByRoomIdOrderByStartDateTimeAsc(Long roomId);

    List<Reservation> findByItemIdOrderByStartDateTimeAsc(Long itemId);

    List<Reservation> findByStartDateTimeBetweenOrderByStartDateTimeAsc(LocalDateTime start, LocalDateTime end);

    List<Reservation> findByRoomIdAndStartDateTimeBetweenOrderByStartDateTimeAsc(
            Long roomId, LocalDateTime start, LocalDateTime end);

    List<Reservation> findByItemIdAndStartDateTimeBetweenOrderByStartDateTimeAsc(
            Long itemId, LocalDateTime start, LocalDateTime end);
}


package com.ucaba.reservas.mapper;

import com.ucaba.reservas.dto.ReservationRequest;
import com.ucaba.reservas.dto.ReservationResponse;
import com.ucaba.reservas.model.Item;
import com.ucaba.reservas.model.Person;
import com.ucaba.reservas.model.Reservation;
import com.ucaba.reservas.model.Room;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    /**
     * Mapper keeps resource type detection in one place for consistency.
     */
    public Reservation toEntity(ReservationRequest request, Person person, Room room, Item item) {
        Reservation reservation = new Reservation();
        reservation.setPerson(person);
        reservation.setRoom(room);
        reservation.setItem(item);
        reservation.setStartDateTime(request.getStartDateTime());
        reservation.setEndDateTime(request.getEndDateTime());
        return reservation;
    }

    public void update(Reservation reservation, ReservationRequest request, Room room, Item item) {
        reservation.setRoom(room);
        reservation.setItem(item);
        reservation.setStartDateTime(request.getStartDateTime());
        reservation.setEndDateTime(request.getEndDateTime());
    }

    public ReservationResponse toResponse(Reservation reservation) {
        String resourceType = reservation.getRoom() != null ? "ROOM" : reservation.getItem() != null ? "ITEM" : "NONE";
        Long resourceId = reservation.getRoom() != null ? reservation.getRoom().getId()
                : reservation.getItem() != null ? reservation.getItem().getId() : null;
        String resourceName = reservation.getRoom() != null ? reservation.getRoom().getName()
                : reservation.getItem() != null ? reservation.getItem().getName() : null;
        return new ReservationResponse(
                reservation.getId(),
                reservation.getPerson().getId(),
                reservation.getPerson().getFullName(),
                resourceType,
                resourceId,
                resourceName,
                reservation.getStartDateTime(),
                reservation.getEndDateTime());
    }
}

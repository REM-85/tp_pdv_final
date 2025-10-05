package com.ucaba.reservas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ucaba.reservas.dto.ReservationRequest;
import com.ucaba.reservas.dto.ReservationResponse;
import com.ucaba.reservas.exception.ConflictException;
import com.ucaba.reservas.model.Person;
import com.ucaba.reservas.model.Room;
import com.ucaba.reservas.model.UserRole;
import com.ucaba.reservas.repository.PersonRepository;
import com.ucaba.reservas.repository.RoomRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoomRepository roomRepository;

    private Person person;
    private Room room;

    @BeforeEach
    void setUp() {
        person = new Person();
        person.setFullName("Test User");
        person.setEmail("test@ucaba.com");
        person.setRole(UserRole.STAFF);
        person = personRepository.save(person);

        room = new Room();
        room.setName("Sala Test");
        room.setCapacity(4);
        room = roomRepository.save(room);
    }

    @Test
    void shouldCreateReservation() {
        ReservationRequest request = buildRequest(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1));
        ReservationResponse response = reservationService.create(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getResourceType()).isEqualTo("ROOM");
    }

    @Test
    void shouldRejectOverlappingReservation() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);
        reservationService.create(buildRequest(start, end));

        assertThatThrownBy(() -> reservationService.create(buildRequest(start.plusMinutes(30), end.plusHours(1))))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("no disponible");
    }

    private ReservationRequest buildRequest(LocalDateTime start, LocalDateTime end) {
        ReservationRequest request = new ReservationRequest();
        request.setPersonId(person.getId());
        request.setRoomId(room.getId());
        request.setStartDateTime(start);
        request.setEndDateTime(end);
        return request;
    }
}


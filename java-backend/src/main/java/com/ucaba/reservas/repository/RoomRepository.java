package com.ucaba.reservas.repository;

import com.ucaba.reservas.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}


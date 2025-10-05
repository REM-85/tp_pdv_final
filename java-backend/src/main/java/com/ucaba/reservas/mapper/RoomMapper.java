package com.ucaba.reservas.mapper;

import com.ucaba.reservas.dto.RoomRequest;
import com.ucaba.reservas.dto.RoomResponse;
import com.ucaba.reservas.model.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public Room toEntity(RoomRequest request) {
        Room room = new Room();
        room.setName(request.getName());
        room.setCapacity(request.getCapacity());
        return room;
    }

    public void update(Room room, RoomRequest request) {
        room.setName(request.getName());
        room.setCapacity(request.getCapacity());
    }

    public RoomResponse toResponse(Room room) {
        return new RoomResponse(room.getId(), room.getName(), room.getCapacity());
    }
}

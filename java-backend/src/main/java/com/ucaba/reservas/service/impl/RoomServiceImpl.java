package com.ucaba.reservas.service.impl;

import com.ucaba.reservas.dto.RoomRequest;
import com.ucaba.reservas.dto.RoomResponse;
import com.ucaba.reservas.exception.NotFoundException;
import com.ucaba.reservas.mapper.RoomMapper;
import com.ucaba.reservas.model.Room;
import com.ucaba.reservas.repository.RoomRepository;
import com.ucaba.reservas.service.RoomService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository repository;
    private final RoomMapper mapper;

    public RoomServiceImpl(RoomRepository repository, RoomMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse findById(Long id) {
        Room room = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sala no encontrada"));
        return mapper.toResponse(room);
    }

    @Override
    public RoomResponse create(RoomRequest request) {
        Room room = mapper.toEntity(request);
        return mapper.toResponse(repository.save(room));
    }

    @Override
    public RoomResponse update(Long id, RoomRequest request) {
        Room room = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sala no encontrada"));
        mapper.update(room, request);
        return mapper.toResponse(repository.save(room));
    }

    @Override
    public void delete(Long id) {
        Room room = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sala no encontrada"));
        repository.delete(room);
    }
}


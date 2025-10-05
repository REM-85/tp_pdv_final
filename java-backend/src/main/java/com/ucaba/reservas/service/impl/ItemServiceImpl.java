package com.ucaba.reservas.service.impl;

import com.ucaba.reservas.dto.ItemRequest;
import com.ucaba.reservas.dto.ItemResponse;
import com.ucaba.reservas.exception.NotFoundException;
import com.ucaba.reservas.mapper.ItemMapper;
import com.ucaba.reservas.model.Item;
import com.ucaba.reservas.repository.ItemRepository;
import com.ucaba.reservas.service.ItemService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final ItemMapper mapper;

    public ItemServiceImpl(ItemRepository repository, ItemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponse findById(Long id) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Articulo no encontrado"));
        return mapper.toResponse(item);
    }

    @Override
    public ItemResponse create(ItemRequest request) {
        Item item = mapper.toEntity(request);
        return mapper.toResponse(repository.save(item));
    }

    @Override
    public ItemResponse update(Long id, ItemRequest request) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Articulo no encontrado"));
        mapper.update(item, request);
        return mapper.toResponse(repository.save(item));
    }

    @Override
    public void delete(Long id) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Articulo no encontrado"));
        repository.delete(item);
    }
}


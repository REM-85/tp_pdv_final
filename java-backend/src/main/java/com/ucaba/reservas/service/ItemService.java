package com.ucaba.reservas.service;

import com.ucaba.reservas.dto.ItemRequest;
import com.ucaba.reservas.dto.ItemResponse;
import java.util.List;

public interface ItemService {
    List<ItemResponse> findAll();

    ItemResponse findById(Long id);

    ItemResponse create(ItemRequest request);

    ItemResponse update(Long id, ItemRequest request);

    void delete(Long id);
}


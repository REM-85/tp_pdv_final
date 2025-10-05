package com.ucaba.reservas.mapper;

import com.ucaba.reservas.dto.ItemRequest;
import com.ucaba.reservas.dto.ItemResponse;
import com.ucaba.reservas.model.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public Item toEntity(ItemRequest request) {
        Item item = new Item();
        item.setName(request.getName());
        item.setAvailable(request.isAvailable());
        return item;
    }

    public void update(Item item, ItemRequest request) {
        item.setName(request.getName());
        item.setAvailable(request.isAvailable());
    }

    public ItemResponse toResponse(Item item) {
        return new ItemResponse(item.getId(), item.getName(), item.isAvailable());
    }
}

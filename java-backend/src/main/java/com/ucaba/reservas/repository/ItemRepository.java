package com.ucaba.reservas.repository;

import com.ucaba.reservas.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}


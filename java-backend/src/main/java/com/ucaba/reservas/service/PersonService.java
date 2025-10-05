package com.ucaba.reservas.service;

import com.ucaba.reservas.dto.PersonRequest;
import com.ucaba.reservas.dto.PersonResponse;
import java.util.List;

public interface PersonService {
    List<PersonResponse> findAll();

    PersonResponse findById(Long id);

    PersonResponse create(PersonRequest request);

    PersonResponse update(Long id, PersonRequest request);

    void delete(Long id);
}


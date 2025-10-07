package com.ucaba.reservas.service.impl;

import com.ucaba.reservas.dto.PersonRequest;
import com.ucaba.reservas.dto.PersonResponse;
import com.ucaba.reservas.exception.BadRequestException;
import com.ucaba.reservas.exception.ConflictException;
import com.ucaba.reservas.exception.NotFoundException;
import com.ucaba.reservas.mapper.PersonMapper;
import com.ucaba.reservas.model.Person;
import com.ucaba.reservas.repository.PersonRepository;
import com.ucaba.reservas.service.PersonService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class PersonServiceImpl implements PersonService {

    private final PersonRepository repository;
    private final PersonMapper mapper;

    public PersonServiceImpl(PersonRepository repository, PersonMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PersonResponse findById(Long id) {
        Person person = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Persona no encontrada"));
        return mapper.toResponse(person);
    }

    @Override
    public PersonResponse create(PersonRequest request) {
        repository.findByEmail(request.getEmail()).ifPresent(existing -> {
            throw new ConflictException("Email ya registrado");
        });
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BadRequestException("La contraseña es obligatoria");
        }
        try {
            Person person = mapper.toEntity(request);
            return mapper.toResponse(repository.save(person));
        } catch (IllegalArgumentException ex) {
            throw new ConflictException("Rol invalido");
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Email duplicado");
        }
    }

    @Override
    public PersonResponse update(Long id, PersonRequest request) {
        Person person = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Persona no encontrada"));
        if (StringUtils.hasText(request.getPassword()) && request.getPassword().length() < 8) {
            throw new BadRequestException("La contraseña debe tener al menos 8 caracteres");
        }
        try {
            mapper.update(person, request);
            return mapper.toResponse(repository.save(person));
        } catch (IllegalArgumentException ex) {
            throw new ConflictException("Rol invalido");
        }
    }

    @Override
    public void delete(Long id) {
        Person person = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Persona no encontrada"));
        repository.delete(person);
    }
}


package com.ucaba.reservas.mapper;

import com.ucaba.reservas.dto.PersonRequest;
import com.ucaba.reservas.dto.PersonResponse;
import com.ucaba.reservas.model.Person;
import com.ucaba.reservas.model.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PersonMapper {

    /**
     * Mapper centralizes enum parsing so controllers remain slim.
     */
    private final PasswordEncoder passwordEncoder;

    public PersonMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Person toEntity(PersonRequest request) {
        Person person = new Person();
        person.setFullName(request.getFullName());
        person.setEmail(request.getEmail());
        person.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        if (StringUtils.hasText(request.getPassword())) {
            person.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        return person;
    }

    public void update(Person person, PersonRequest request) {
        person.setFullName(request.getFullName());
        person.setEmail(request.getEmail());
        person.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        if (StringUtils.hasText(request.getPassword())) {
            person.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
    }

    public PersonResponse toResponse(Person person) {
        return new PersonResponse(
                person.getId(),
                person.getFullName(),
                person.getEmail(),
                person.getRole().name());
    }
}

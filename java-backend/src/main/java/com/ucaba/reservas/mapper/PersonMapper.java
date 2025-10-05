package com.ucaba.reservas.mapper;

import com.ucaba.reservas.dto.PersonRequest;
import com.ucaba.reservas.dto.PersonResponse;
import com.ucaba.reservas.model.Person;
import com.ucaba.reservas.model.UserRole;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    /**
     * Mapper centralizes enum parsing so controllers remain slim.
     */
    public Person toEntity(PersonRequest request) {
        Person person = new Person();
        person.setFullName(request.getFullName());
        person.setEmail(request.getEmail());
        person.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        return person;
    }

    public void update(Person person, PersonRequest request) {
        person.setFullName(request.getFullName());
        person.setEmail(request.getEmail());
        person.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
    }

    public PersonResponse toResponse(Person person) {
        return new PersonResponse(
                person.getId(),
                person.getFullName(),
                person.getEmail(),
                person.getRole().name());
    }
}

package com.ucaba.reservas.config;

import com.ucaba.reservas.model.Person;
import com.ucaba.reservas.repository.PersonRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
public class PasswordBootstrap implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PasswordBootstrap.class);

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordBootstrap(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<Person> people = personRepository.findAll();
        for (Person person : people) {
            String hash = person.getPasswordHash();
            if (StringUtils.hasText(hash) && hash.startsWith("RAW:")) {
                String rawPassword = hash.substring(4);
                person.setPasswordHash(passwordEncoder.encode(rawPassword));
                log.info("Inicializando password para {}", person.getEmail());
            }
        }
    }
}

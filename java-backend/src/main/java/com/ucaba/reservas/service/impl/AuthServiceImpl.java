package com.ucaba.reservas.service.impl;

import com.ucaba.reservas.dto.AuthRequest;
import com.ucaba.reservas.dto.AuthResponse;
import com.ucaba.reservas.dto.RegisterRequest;
import com.ucaba.reservas.exception.BadRequestException;
import com.ucaba.reservas.exception.ConflictException;
import com.ucaba.reservas.mapper.PersonMapper;
import com.ucaba.reservas.model.Person;
import com.ucaba.reservas.repository.PersonRepository;
import com.ucaba.reservas.security.JwtTokenProvider;
import com.ucaba.reservas.security.UserPrincipal;
import com.ucaba.reservas.service.AuthService;
import java.time.Duration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(PersonRepository personRepository,
                           PersonMapper personMapper,
                           JwtTokenProvider tokenProvider,
                           AuthenticationManager authenticationManager) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        personRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            throw new ConflictException("Email ya registrado");
        });
        try {
            Person person = personMapper.toEntity(toPersonRequest(request));
            Person saved = personRepository.save(person);
            UserPrincipal principal = new UserPrincipal(saved);
            String token = tokenProvider.generateToken(principal);
            long expiresIn = Duration.ofMinutes(tokenProvider.getExpirationMinutes()).toSeconds();
            return new AuthResponse(token, expiresIn, saved.getFullName(), saved.getEmail(), saved.getRole().name());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Rol invalido");
        }
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = tokenProvider.generateToken(principal);
        long expiresIn = Duration.ofMinutes(tokenProvider.getExpirationMinutes()).toSeconds();
        String role = principal.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        return new AuthResponse(token, expiresIn, principal.getFullName(), principal.getUsername(), role);
    }

    private com.ucaba.reservas.dto.PersonRequest toPersonRequest(RegisterRequest request) {
        com.ucaba.reservas.dto.PersonRequest personRequest = new com.ucaba.reservas.dto.PersonRequest();
        personRequest.setFullName(request.getFullName());
        personRequest.setEmail(request.getEmail());
        personRequest.setRole(request.getRole());
        personRequest.setPassword(request.getPassword());
        return personRequest;
    }
}

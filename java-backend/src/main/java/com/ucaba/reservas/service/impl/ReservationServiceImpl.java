package com.ucaba.reservas.service.impl;

import com.ucaba.reservas.dto.AvailabilityResponse;
import com.ucaba.reservas.dto.HistoryPoint;
import com.ucaba.reservas.dto.HistoryResponse;
import com.ucaba.reservas.dto.ReservationRequest;
import com.ucaba.reservas.dto.ReservationResponse;
import com.ucaba.reservas.exception.BadRequestException;
import com.ucaba.reservas.exception.ConflictException;
import com.ucaba.reservas.exception.NotFoundException;
import com.ucaba.reservas.mapper.ReservationMapper;
import com.ucaba.reservas.model.Item;
import com.ucaba.reservas.model.Person;
import com.ucaba.reservas.model.Reservation;
import com.ucaba.reservas.model.Room;
import com.ucaba.reservas.repository.ItemRepository;
import com.ucaba.reservas.repository.PersonRepository;
import com.ucaba.reservas.repository.ReservationRepository;
import com.ucaba.reservas.repository.RoomRepository;
import com.ucaba.reservas.service.ReservationService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final PersonRepository personRepository;
    private final RoomRepository roomRepository;
    private final ItemRepository itemRepository;
    private final ReservationMapper mapper;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  PersonRepository personRepository,
                                  RoomRepository roomRepository,
                                  ItemRepository itemRepository,
                                  ReservationMapper mapper) {
        this.reservationRepository = reservationRepository;
        this.personRepository = personRepository;
        this.roomRepository = roomRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .sorted(Comparator.comparing(Reservation::getStartDateTime))
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
        return mapper.toResponse(reservation);
    }

    @Override
    public ReservationResponse create(ReservationRequest request) {
        validateRequest(request);
        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new NotFoundException("Persona no encontrada"));
        ResourceHolder resource = resolveResource(request.getRoomId(), request.getItemId());
        ensureAvailability(resource, request.getStartDateTime(), request.getEndDateTime(), null);
        Reservation reservation = mapper.toEntity(request, person, resource.room(), resource.item());
        return mapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse update(Long id, ReservationRequest request) {
        validateRequest(request);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
        if (!Objects.equals(reservation.getPerson().getId(), request.getPersonId())) {
            Person person = personRepository.findById(request.getPersonId())
                    .orElseThrow(() -> new NotFoundException("Persona no encontrada"));
            reservation.setPerson(person);
        }
        ResourceHolder resource = resolveResource(request.getRoomId(), request.getItemId());
        ensureAvailability(resource, request.getStartDateTime(), request.getEndDateTime(), id);
        mapper.update(reservation, request, resource.room(), resource.item());
        return mapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public void delete(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
        reservationRepository.delete(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public AvailabilityResponse checkAvailability(String resourceType, Long resourceId,
                                                   LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new BadRequestException("Rango de fechas invalido");
        }
        ResourceHolder holder = resolveResource(resourceType, resourceId);
        boolean available = isAvailable(holder, start, end, null);
        return new AvailabilityResponse(holder.type(), resourceId, available, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponse buildHistory(String resourceType, Long resourceId,
                                        LocalDate startDate, LocalDate endDate) {
        ResourceHolder holder = resolveResource(resourceType, resourceId);
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusMonths(6);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        if (!start.isBefore(end.plusDays(1))) {
            throw new BadRequestException("Rango historico invalido");
        }
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        List<Reservation> reservations;
        if (holder.room() != null) {
            reservations = reservationRepository.findByRoomIdAndStartDateTimeBetweenOrderByStartDateTimeAsc(
                    holder.room().getId(), startDateTime, endDateTime);
        } else {
            reservations = reservationRepository.findByItemIdAndStartDateTimeBetweenOrderByStartDateTimeAsc(
                    holder.item().getId(), startDateTime, endDateTime);
        }
        Map<LocalDate, Long> counts = reservations.stream()
                .collect(Collectors.groupingBy(res -> res.getStartDateTime().toLocalDate(), Collectors.counting()));
        List<HistoryPoint> points = start.datesUntil(end.plusDays(1))
                .map(date -> new HistoryPoint(date.atStartOfDay(), counts.getOrDefault(date, 0L)))
                .collect(Collectors.toList());
        return new HistoryResponse(holder.type() + "-" + resourceId,
                holder.type(),
                resourceId,
                points);
    }

    private void validateRequest(ReservationRequest request) {
        if (request.getRoomId() == null && request.getItemId() == null) {
            throw new BadRequestException("Debe indicar sala o articulo");
        }
        if (request.getRoomId() != null && request.getItemId() != null) {
            throw new BadRequestException("No se puede reservar sala y articulo en la misma reserva");
        }
        if (request.getStartDateTime() == null || request.getEndDateTime() == null
                || !request.getStartDateTime().isBefore(request.getEndDateTime())) {
            throw new BadRequestException("Fechas invalidas");
        }
    }

    private ResourceHolder resolveResource(Long roomId, Long itemId) {
        if (roomId != null) {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new NotFoundException("Sala no encontrada"));
            return new ResourceHolder("ROOM", room, null);
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Articulo no encontrado"));
        if (!item.isAvailable()) {
            throw new ConflictException("Articulo marcado como no disponible");
        }
        return new ResourceHolder("ITEM", null, item);
    }

    private ResourceHolder resolveResource(String resourceType, Long resourceId) {
        if (resourceType == null || resourceId == null) {
            throw new BadRequestException("Recurso invalido");
        }
        String normalized = resourceType.toUpperCase(Locale.ROOT);
        if ("ROOM".equals(normalized)) {
            Room room = roomRepository.findById(resourceId)
                    .orElseThrow(() -> new NotFoundException("Sala no encontrada"));
            return new ResourceHolder("ROOM", room, null);
        } else if ("ITEM".equals(normalized)) {
            Item item = itemRepository.findById(resourceId)
                    .orElseThrow(() -> new NotFoundException("Articulo no encontrado"));
            if (!item.isAvailable()) {
                throw new ConflictException("Articulo marcado como no disponible");
            }
            return new ResourceHolder("ITEM", null, item);
        }
        throw new BadRequestException("Tipo de recurso desconocido");
    }

    private void ensureAvailability(ResourceHolder holder, LocalDateTime start, LocalDateTime end, Long reservationId) {
        if (!isAvailable(holder, start, end, reservationId)) {
            throw new ConflictException("Recurso no disponible en el horario solicitado");
        }
    }

    private boolean isAvailable(ResourceHolder holder, LocalDateTime start, LocalDateTime end, Long reservationId) {
        List<Reservation> overlaps;
        if (holder.room() != null) {
            overlaps = reservationRepository.findByRoomIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                    holder.room().getId(), end, start);
        } else {
            overlaps = reservationRepository.findByItemIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                    holder.item().getId(), end, start);
        }
        return overlaps.stream().noneMatch(res -> reservationId == null || !res.getId().equals(reservationId));
    }

    private record ResourceHolder(String type, Room room, Item item) {
    }
}

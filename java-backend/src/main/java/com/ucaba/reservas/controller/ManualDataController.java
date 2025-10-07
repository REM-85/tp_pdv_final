package com.ucaba.reservas.controller;

import com.ucaba.reservas.dto.ItemRequest;
import com.ucaba.reservas.dto.ReservationRequest;
import com.ucaba.reservas.dto.RoomRequest;
import com.ucaba.reservas.service.ItemService;
import com.ucaba.reservas.service.PersonService;
import com.ucaba.reservas.service.ReservationService;
import com.ucaba.reservas.service.RoomService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/manual")
public class ManualDataController {

    private final RoomService roomService;
    private final ItemService itemService;
    private final ReservationService reservationService;
    private final PersonService personService;

    public ManualDataController(RoomService roomService,
                                ItemService itemService,
                                ReservationService reservationService,
                                PersonService personService) {
        this.roomService = roomService;
        this.itemService = itemService;
        this.reservationService = reservationService;
        this.personService = personService;
    }

    @GetMapping
    public String dashboard(@RequestParam(value = "tab", defaultValue = "rooms") String tab, Model model) {
        populateModel(model);
        ensureForms(model);
        model.addAttribute("activeTab", tab);
        return "manual/dashboard";
    }

    @PostMapping("/rooms")
    public String createRoom(@Valid @ModelAttribute("roomForm") RoomRequest request,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            populateModel(model);
            ensureForms(model);
            model.addAttribute("activeTab", "rooms");
            return "manual/dashboard";
        }
        try {
            roomService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Sala creada correctamente");
            return "redirect:/manual?tab=rooms";
        } catch (RuntimeException ex) {
            bindingResult.reject("error", ex.getMessage());
            populateModel(model);
            ensureForms(model);
            model.addAttribute("activeTab", "rooms");
            return "manual/dashboard";
        }
    }

    @PostMapping("/rooms/{id}/update")
    public String updateRoom(@PathVariable Long id,
                             @RequestParam String name,
                             @RequestParam int capacity,
                             RedirectAttributes redirectAttributes) {
        RoomRequest request = new RoomRequest();
        request.setName(name);
        request.setCapacity(capacity);
        try {
            roomService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Sala actualizada");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/manual?tab=rooms";
    }

    @PostMapping("/items")
    public String createItem(@Valid @ModelAttribute("itemForm") ItemRequest request,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            populateModel(model);
            ensureForms(model);
            model.addAttribute("activeTab", "items");
            return "manual/dashboard";
        }
        try {
            itemService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Recurso creado correctamente");
            return "redirect:/manual?tab=items";
        } catch (RuntimeException ex) {
            bindingResult.reject("error", ex.getMessage());
            populateModel(model);
            ensureForms(model);
            model.addAttribute("activeTab", "items");
            return "manual/dashboard";
        }
    }

    @PostMapping("/items/{id}/update")
    public String updateItem(@PathVariable Long id,
                             @RequestParam String name,
                             @RequestParam(defaultValue = "false") boolean available,
                             RedirectAttributes redirectAttributes) {
        ItemRequest request = new ItemRequest();
        request.setName(name);
        request.setAvailable(available);
        try {
            itemService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Recurso actualizado");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/manual?tab=items";
    }

    @PostMapping("/reservations")
    public String createReservation(@Valid @ModelAttribute("reservationForm") ReservationRequest request,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            populateModel(model);
            ensureForms(model);
            model.addAttribute("activeTab", "reservations");
            return "manual/dashboard";
        }
        try {
            reservationService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva registrada");
            return "redirect:/manual?tab=reservations";
        } catch (RuntimeException ex) {
            bindingResult.reject("error", ex.getMessage());
            populateModel(model);
            ensureForms(model);
            model.addAttribute("activeTab", "reservations");
            return "manual/dashboard";
        }
    }

    @PostMapping("/reservations/{id}/update")
    public String updateReservation(@PathVariable Long id,
                                    @RequestParam Long personId,
                                    @RequestParam String resourceType,
                                    @RequestParam Long resourceId,
                                    @RequestParam("start")
                                    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime start,
                                    @RequestParam("end")
                                    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime end,
                                    RedirectAttributes redirectAttributes) {
        ReservationRequest request = new ReservationRequest();
        request.setPersonId(personId);
        if ("ROOM".equalsIgnoreCase(resourceType)) {
            request.setRoomId(resourceId);
            request.setItemId(null);
        } else {
            request.setItemId(resourceId);
            request.setRoomId(null);
        }
        request.setStartDateTime(start);
        request.setEndDateTime(end);
        try {
            reservationService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva actualizada");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/manual?tab=reservations";
    }

    private void populateModel(Model model) {
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("items", itemService.findAll());
        model.addAttribute("reservations", reservationService.findAll());
        model.addAttribute("people", personService.findAll());
    }

    private void ensureForms(Model model) {
        if (!model.containsAttribute("roomForm")) {
            model.addAttribute("roomForm", new RoomRequest());
        }
        if (!model.containsAttribute("itemForm")) {
            model.addAttribute("itemForm", new ItemRequest());
        }
        if (!model.containsAttribute("reservationForm")) {
            ReservationRequest form = new ReservationRequest();
            model.addAttribute("reservationForm", form);
        }
    }
}

package com.vimal.code.ToDo.controllers;

import com.vimal.code.ToDo.dto.req.EventreqDto;
import com.vimal.code.ToDo.dto.resp.EventRespDto;
import com.vimal.code.ToDo.models.Event;
import com.vimal.code.ToDo.service.EventMapper;
import com.vimal.code.ToDo.service.EventService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody EventreqDto dto) {
        try {
            Event event = eventService.createEvent(dto);
            EventRespDto response = eventMapper.toDto(event);
            return status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création de l'événement : " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody EventreqDto dto) {
        try {
            Event updated = eventService.updateEvent(id, dto);
            EventRespDto response = eventMapper.toDto(updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<EventRespDto> getEventById(@PathVariable long id) {
        logger.info("Fetching event with ID: {}", id);
        try {
            return eventService.getEventById(id)
                    .map(event -> {
                        logger.info("Event found: {}", event);
                        return ResponseEntity.ok(event);
                    })
                    .orElseGet(() -> {
                        logger.warn("Event not found for ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error fetching event with ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable long id) {
        eventService.deleteEvent(id);
        return ok("Événement supprimé avec succès.");
    }

    @PutMapping("/participer/{id}")
    public ResponseEntity<?> participerEvent(@PathVariable long id, Authentication authentication) {
        try {
            Event event = eventService.inscriptionEvent(id, authentication);
            EventRespDto response = eventMapper.toDto(event);
            return ok(response);
        } catch (EntityNotFoundException ex) {
            return status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}

package com.vimal.code.ToDo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vimal.code.ToDo.dto.req.EventreqDto;
import com.vimal.code.ToDo.dto.resp.EventRespDto;
import com.vimal.code.ToDo.models.Event;
import com.vimal.code.ToDo.service.EventMapper;
import com.vimal.code.ToDo.service.EventService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody EventreqDto dto, Authentication authentication) {
        try {
            Event event = eventService.createEvent(dto, authentication);
            EventRespDto response = eventMapper.toDto(event);
            logger.info("Event created successfully: {}", response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (JsonProcessingException e) {
            logger.error("Error during reverse geocoding: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la géolocalisation: " + e.getMessage());

        } catch (Exception e) {
            logger.error("Error creating event: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création de l'événement: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody EventreqDto dto) {
        try {
            Event updated = eventService.updateEvent(id, dto);
            EventRespDto response = eventMapper.toDto(updated);
            logger.info("Event updated successfully: ID {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.warn("Event not found for update: ID {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating event with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour de l'événement: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<EventRespDto> getEventById(@PathVariable long id) {
        logger.info("Fetching event with ID: {}", id);
        return eventService.getEventById(id)
                .map(event -> {
                    EventRespDto response = eventMapper.toDto(event);
                    logger.info("Event found: {}", response);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    logger.warn("Event not found for ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<EventRespDto>> getAllEvents(Authentication authentication) {
        try {
            List<Event> events = eventService.getAllEvents(authentication);
            List<EventRespDto> response = events.stream()
                    .map(eventMapper::toDto)
                    .collect(Collectors.toList());
            logger.info("Fetched {} events", response.size());
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Error fetching all events: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable long id) {
        try {
            eventService.deleteEvent(id);
            logger.info("Event deleted successfully: ID {}", id);
            return ResponseEntity.ok("Événement supprimé avec succès.");
        } catch (Exception e) {
            logger.error("Error deleting event with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression de l'événement: " + e.getMessage());
        }
    }

    @PutMapping("/participer/{id}")
    public ResponseEntity<?> participerEvent(@PathVariable long id, Authentication authentication) {
        try {
            Event event = eventService.inscriptionEvent(id, authentication);
            EventRespDto response = eventMapper.toDto(event);
            logger.info("User successfully participated in event: ID {}", id);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            logger.warn("Error during participation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error during participation in event ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'inscription à l'événement: " + e.getMessage());
        }
    }

    @PostMapping("/validate/{id}")
    public ResponseEntity<?> validateEvent(@PathVariable long id) {
        try {
            Event event = eventService.validateEvent(id);
            EventRespDto response = eventMapper.toDto(event);
            logger.info("Event validated successfully: ID {}", id);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            logger.warn("Event already validated: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error validating event with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la validation de l'événement: " + e.getMessage());
        }
    }
    @GetMapping("/my-events")
    public ResponseEntity<List<EventRespDto>> getMyEvents(Authentication authentication) {
        try {
            List<Event> events = eventService.getEventsByUser(authentication);
            List<EventRespDto> response = events.stream()
                    .map(eventMapper::toDto)
                    .collect(Collectors.toList());
            logger.info("Fetched {} events for authenticated user", response.size());
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            logger.warn("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid authentication: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Error fetching events for authenticated user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
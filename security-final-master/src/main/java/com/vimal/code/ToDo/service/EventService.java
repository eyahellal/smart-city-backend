package com.vimal.code.ToDo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vimal.code.ToDo.Repositories.CitoyenRepository;
import com.vimal.code.ToDo.Repositories.EventRepository;
import com.vimal.code.ToDo.Repositories.UserRepo;
import com.vimal.code.ToDo.dto.req.EventreqDto;
import com.vimal.code.ToDo.dto.resp.EventRespDto;
import com.vimal.code.ToDo.models.Citoyen;
import com.vimal.code.ToDo.models.Event;
import com.vimal.code.ToDo.models.Role;
import com.vimal.code.ToDo.models.UserEnitiy;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CitoyenRepository citoyenRepository;

    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private GeoService geoService;
    @Autowired
    private UserRepo userRepository;


    public Event createEvent(EventreqDto dto, Authentication authentication) throws JsonProcessingException {
        // Get the authenticated user
        String email = authentication.getName();
        UserEnitiy createdBy = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Citoyen non trouvé pour l'email: " + email));

        // Map DTO to entity
        Event event = eventMapper.toEntity(dto);
        event.setCreatedBy(createdBy);

        // Reverse geocode and extract location name
        String reverseGeoJson = geoService.reverseGeocode(event.getLatitude(), event.getLongitude());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(reverseGeoJson);
        String displayName = root.path("display_name").asText();
        event.setLieuName(displayName);

        // Set validation status based on role
        if (createdBy.getRole()== Role.AGENT) {
            event.setValidated(true);
        } else {
            event.setValidated(false);

        }

        return eventRepository.save(event);
    }
    public Event updateEvent(Long id, EventreqDto dto) {
        return eventRepository.findById(id)
                .map(event -> {
                    event.setNom(dto.getNom());
                    event.setDescription(dto.getDescription());
                    event.setDate(dto.getDate());
                    event.setLatitude(dto.getLatitude());
                    event.setLongitude(dto.getLongitude());
                    //event.setLieuName(dto.getLieuName());
                    return eventRepository.save(event);
                })
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'ID: " + id));
    }

    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }
    @Transactional

    public Event inscriptionEvent(Long eventId, Authentication authentication) {
        String email = authentication.getName();

        Citoyen citoyen = citoyenRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Citoyen non trouvé pour l'email: " + email));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Événement introuvable avec l'ID: " + eventId));

        if (event.getParticipants() == null) {
            event.setParticipants(new ArrayList<>());
        }

        if (!event.getParticipants().contains(citoyen)) {
            event.getParticipants().add(citoyen);
        }

        return eventRepository.save(event);
    }
    public List<Event> getAllEvents(Authentication authentication) {
        String email = authentication.getName();
        UserEnitiy user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé pour l'email: " + email));

        if (user.getRole()==Role.ADMIN){
            return eventRepository.findByValidatedFalse();
        } else {
            return eventRepository.findByValidatedTrue();

        }
    }
    public Optional<Event> getEventById(long id) {
        return eventRepository.findById(id);
    }
    @Transactional
    public Event validateEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Événement introuvable avec l'ID: " + eventId));

        if (event.isValidated()) {
            throw new IllegalStateException("Cet événement est déjà validé.");
        }

        event.setValidated(true);
        return eventRepository.save(event);
    }
    public List<Event> getEventsByUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Authentication or user email cannot be null");
        }

        String email = authentication.getName();
        UserEnitiy user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé pour l'email: " + email));

        return eventRepository.findByCreatedBy(user);
    }
}

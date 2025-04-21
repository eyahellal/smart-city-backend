package com.vimal.code.ToDo.service;

import com.vimal.code.ToDo.Repositories.CitoyenRepository;
import com.vimal.code.ToDo.Repositories.EventRepository;
import com.vimal.code.ToDo.dto.req.EventreqDto;
import com.vimal.code.ToDo.dto.resp.EventRespDto;
import com.vimal.code.ToDo.models.Citoyen;
import com.vimal.code.ToDo.models.Event;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CitoyenRepository citoyenRepository;

    @Autowired
    private EventMapper eventMapper;

    public Event createEvent(EventreqDto dto) {
        Event event = eventMapper.toEntity(dto);
        return eventRepository.save(event);
    }

    public Optional<EventRespDto> getEventById(long id) {
        return eventRepository.findById(id)
                .map(eventMapper::toDto);
    }

    public Event updateEvent(Long id, EventreqDto dto) {
        return eventRepository.findById(id)
                .map(event -> {
                    event.setNom(dto.getNom());
                    event.setDescription(dto.getDescription());
                    event.setDate(dto.getDate());
                    event.setLatitude(dto.getLatitude());
                    event.setLongitude(dto.getLongitude());
                    event.setLieuName(dto.getLieuName());
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
}

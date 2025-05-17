package com.vimal.code.ToDo.controllers;

import com.vimal.code.ToDo.dto.resp.AgentStatsDTO;
import com.vimal.code.ToDo.models.Agent;
import com.vimal.code.ToDo.models.Event;
import com.vimal.code.ToDo.models.UserEnitiy;
import com.vimal.code.ToDo.service.AgentStatsService;
import com.vimal.code.ToDo.service.EventService;
import com.vimal.code.ToDo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/agent-stats")
@RequiredArgsConstructor
public class AgentController {

    private final AgentStatsService agentStatsService;
    private final EventService eventService;
    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);
    @Autowired
    private final UserService userService;

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ROLE_AGENT')")
    public ResponseEntity<?> getAgentStats(Authentication authentication) {
        try {
            String agentEmail = authentication.getName();
            logger.info("Fetching stats for agent with email: {}", agentEmail);

            AgentStatsDTO stats = agentStatsService.getAgentStats(agentEmail, authentication);
            if (stats == null) {
                logger.warn("Failed to fetch stats for agent: {}", agentEmail);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Impossible de récupérer les statistiques pour l'agent.");
            }

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error fetching agent stats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des statistiques de l'agent: " + e.getMessage());
        }
    }

    @GetMapping("/event-count/{eventId}")
    @PreAuthorize("hasAuthority('ROLE_AGENT')")
    public ResponseEntity<String> getParticipantCount(@PathVariable Long eventId, Authentication authentication) {
        try {
            String agentEmail = authentication.getName();
            UserEnitiy agentOpt = userService.findByEmail(agentEmail);
            if (agentOpt==null) {
                logger.warn("Agent not found for email: {}", agentEmail);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Agent non trouvé pour l'email : " + agentEmail);
            }

            Optional<Event> eventOpt = eventService.getEventById(eventId);
            if (eventOpt.isEmpty()) {
                logger.warn("Event not found with ID: {}", eventId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Événement introuvable avec l'ID: " + eventId);
            }

            Event event = eventOpt.get();
            if (!event.getCreatedBy().getEmail().equals(agentEmail)) {
                logger.warn("Agent {} does not have access to event {}", agentEmail, eventId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Vous n'avez pas l'autorisation d'accéder à cet événement.");
            }

            int participantCount = eventService.getParticipantCount(event);
            logger.info("Participant count for event {}: {}", eventId, participantCount);

            return ResponseEntity.ok(String.valueOf(participantCount));
        } catch (Exception e) {
            logger.error("Error fetching participant count for event {}: {}", eventId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération du nombre de participants: " + e.getMessage());
        }
    }
}
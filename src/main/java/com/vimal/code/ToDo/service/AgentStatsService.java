package com.vimal.code.ToDo.service;

import com.vimal.code.ToDo.Repositories.AgentRepository;
import com.vimal.code.ToDo.dto.resp.AgentStatsDTO;
import com.vimal.code.ToDo.dto.resp.EventDTO;
import com.vimal.code.ToDo.dto.resp.ResolutionTrendDTO;
import com.vimal.code.ToDo.models.Agent;
import com.vimal.code.ToDo.models.Event;
import com.vimal.code.ToDo.models.Reclamation;
import com.vimal.code.ToDo.models.ServiceUrbain;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentStatsService {

    private static final Logger logger = LoggerFactory.getLogger(AgentStatsService.class);

    private final ReclamationService reclamationService;
    private final EventService eventService;
    private final AgentService agentService;
    @Autowired
    private AgentRepository agentRepository;
    public List<Object[]> countAgentsByServiceType() {
        return agentRepository.countAgentsByServiceType();
    }

    public AgentStatsDTO getAgentStats(String agentEmail, Authentication authentication) {
        logger.info("Fetching stats for agent with email: {}", agentEmail);

        Optional<Agent> agentOpt = agentService.findByEmail(agentEmail);
        if (agentOpt.isEmpty()) {
            logger.warn("Agent not found for email: {}", agentEmail);
            return null; // Controller will handle the null case
        }
        Agent agent = agentOpt.get();

        ServiceUrbain service = agent.getServiceUrbain();
        if (service == null) {
            logger.warn("Agent found but has no associated service: {}", agentEmail);
            return null;
        }

        List<Reclamation> reclamations = reclamationService.getReclamationsByService(service.getType());
        logger.info("Fetched {} reclamations for service: {}", reclamations.size(), service.getType());

        AgentStatsDTO stats = new AgentStatsDTO();
        stats.setTotalAssignedReclamations(reclamations.size());
        stats.setPendingReclamations((int) reclamations.stream()
                .filter(r -> !r.isResolu()).count());
        stats.setResolvedReclamations((int) reclamations.stream()
                .filter(Reclamation::isResolu).count());

        List<Event> events = eventService.getEventsByUser(authentication);
        List<EventDTO> eventDTOs = events.stream().map(event -> {
            EventDTO dto = new EventDTO();
            dto.setName(event.getDescription());
            int participantCount = eventService.getParticipantCount(event);
            dto.setNumberOfParticipants(participantCount);
            return dto;
        }).collect(Collectors.toList());
        stats.setManagedEvents(eventDTOs);
        logger.info("Fetched {} events for agent: {}", eventDTOs.size(), agentEmail);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        List<ResolutionTrendDTO> resolutionTrend = calculateResolutionTrend(reclamations, startDate, endDate);
        stats.setResolutionTrend(resolutionTrend);
        logger.info("Calculated resolution trend for agent: {} entries", resolutionTrend.size());

        return stats;
    }

    private List<ResolutionTrendDTO> calculateResolutionTrend(List<Reclamation> reclamations, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Long> resolvedByDate = reclamations.stream()
                .filter(Reclamation::isResolu)
                .filter(r -> {
                    LocalDate createdDate = r.getDateCreation().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return !createdDate.isBefore(startDate) && !createdDate.isAfter(endDate);
                })
                .collect(Collectors.groupingBy(
                        r -> r.getDateCreation().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        Collectors.counting()
                ));

        List<ResolutionTrendDTO> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            ResolutionTrendDTO dto = new ResolutionTrendDTO();
            dto.setDate(date.format(formatter));
            dto.setResolvedCount(resolvedByDate.getOrDefault(date, 0L).intValue());
            trend.add(dto);
        }
        return trend;
    }
}
package com.vimal.code.ToDo.service;


import com.vimal.code.ToDo.Repositories.EventRepository;
import com.vimal.code.ToDo.Repositories.ReclamationRepository;
import com.vimal.code.ToDo.Repositories.UserRepo;
import com.vimal.code.ToDo.dto.resp.AdminStatsDTO;
import com.vimal.code.ToDo.dto.resp.ResolutionTrendDTO;
import com.vimal.code.ToDo.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReclamationRepository reclamationRepository;

    public AdminStatsDTO getAdminStats() {
        AdminStatsDTO stats = new AdminStatsDTO();

        // 1. Count users with role CITOYEN
        long totalCitizens = 0;
        try {
            totalCitizens = userRepository.countByRole(Role.CITOYEN);
            System.out.println("Counted citizens with role CITOYEN: " + totalCitizens);
        } catch (Exception e) {
            System.out.println("Error counting citizens: " + e.getMessage());
        }
        stats.setTotalCitizens(totalCitizens);

        // 2. Count total events and calculate participation rate
        long totalEvents = eventRepository.count();
        long totalParticipants = eventRepository.countParticipants();
        long expectedParticipants = totalEvents > 0 ? totalEvents * 10 : 1;
        long participationRate = totalEvents > 0
                ? Math.min(100, Math.max(0, Math.round((double) totalParticipants / expectedParticipants * 100)))
                : 0;
        stats.setTotalEvents(totalEvents);
        stats.setParticipationRate(participationRate);

        // 3. Count total, resolved, and unresolved reclamations
        long totalReclamations = reclamationRepository.count();
        long resolvedReclamations = reclamationRepository.countByResolu(true);
        long unresolvedReclamations = totalReclamations - resolvedReclamations;
        stats.setTotalReclamations(totalReclamations);
        stats.setResolvedReclamations(resolvedReclamations);
        stats.setUnresolvedReclamations(unresolvedReclamations);

        // 4. Efficiency trend (reclamations created over the last 7 days)
        LocalDate today = LocalDate.now();
        List<ResolutionTrendDTO> efficiencyTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            int createdOnDate = (int) reclamationRepository.countByDateCreation(date);
            ResolutionTrendDTO trendEntry = new ResolutionTrendDTO();
            trendEntry.setDate(date.toString());
            trendEntry.setResolvedCount(createdOnDate);
            efficiencyTrend.add(trendEntry);
        }
        stats.setEfficiencyTrend(efficiencyTrend);

        return stats;
    }
}
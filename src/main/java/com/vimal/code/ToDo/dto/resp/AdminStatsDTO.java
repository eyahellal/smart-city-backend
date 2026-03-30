package com.vimal.code.ToDo.dto.resp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class AdminStatsDTO {
    private long totalCitizens;
    private long totalEvents;
    private long participationRate;
    private long totalReclamations;
    private long resolvedReclamations;
    private long unresolvedReclamations;
    private List<ResolutionTrendDTO> efficiencyTrend;}
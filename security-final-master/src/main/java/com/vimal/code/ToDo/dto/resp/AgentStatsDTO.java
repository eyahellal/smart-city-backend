package com.vimal.code.ToDo.dto.resp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@RequiredArgsConstructor
public class AgentStatsDTO {
    private int totalAssignedReclamations;
    private int pendingReclamations;
    private int resolvedReclamations;
    private List<EventDTO> managedEvents;
    private List<ResolutionTrendDTO> resolutionTrend;
}

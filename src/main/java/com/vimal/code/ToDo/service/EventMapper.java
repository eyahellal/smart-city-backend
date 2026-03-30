package com.vimal.code.ToDo.service;

import com.vimal.code.ToDo.dto.req.EventreqDto;
import com.vimal.code.ToDo.dto.resp.EventRespDto;
import com.vimal.code.ToDo.models.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    // DTO → Entity
    public Event toEntity(EventreqDto dto) {
        Event event = new Event();
        event.setNom(dto.getNom());
        event.setDescription(dto.getDescription());
        event.setLieuName(dto.getLieuName());
        event.setLatitude(dto.getLatitude());
        event.setLongitude(dto.getLongitude());
        event.setDate(dto.getDate());
        return event;
    }

    // Entity → DTO
    public EventRespDto toDto(Event event) {
        EventRespDto dto = new EventRespDto();
        dto.setNom(event.getNom());
        dto.setDescription(event.getDescription());
        dto.setLatitude(event.getLatitude());
        dto.setLongitude(event.getLongitude());
        dto.setLieuName(event.getLieuName());
        dto.setDate(event.getDate());
        dto.setId(event.getId());

        return dto;
    }
}

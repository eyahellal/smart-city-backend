package com.vimal.code.ToDo.dto.resp;

import com.vimal.code.ToDo.models.ServiceUrbain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@AllArgsConstructor
@Getter
@Setter
public class AgentResponseDto {
    private String name;
    private String email;
    private ServiceUrbain serviceUrbain;
    private long id;}



package com.vimal.code.ToDo.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventRespDto {
    private String nom;
    private String description;
    private double latitude;
    private double longitude;
    private String lieuName;
    private Date date;
    private Long id;


}


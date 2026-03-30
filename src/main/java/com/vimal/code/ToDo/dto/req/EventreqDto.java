package com.vimal.code.ToDo.dto.req;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class EventreqDto {
    private String nom;
    private String description;
    private double latitude;
    private double longitude;
    private String lieuName;
    private Date date;
}

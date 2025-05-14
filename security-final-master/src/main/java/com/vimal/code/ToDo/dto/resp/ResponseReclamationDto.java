package com.vimal.code.ToDo.dto.resp;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ResponseReclamationDto {
    private Long id;
    private boolean resolu;
    private String image;
    private String description;
    private Date dateCreation;
    private String serviceUrbainType;
    private String citoyenName;
    private double longitude;
    private double latitude;

    }
//    private String serviceUrbainType;


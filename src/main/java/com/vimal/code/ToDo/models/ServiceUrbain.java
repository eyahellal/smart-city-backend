package com.vimal.code.ToDo.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ServiceUrbain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private ServiceType type;

}

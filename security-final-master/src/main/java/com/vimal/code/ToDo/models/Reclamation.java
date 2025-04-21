package com.vimal.code.ToDo.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reclamation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean resolu;
    private String image;
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    @ManyToOne
    @JoinColumn(name = "user_db_id", nullable = true)
    private UserEnitiy citoyen;

    @ManyToOne
    @JoinColumn(name = "service_urbain_id", nullable = true)
    private ServiceUrbain serviceResponsable;
}




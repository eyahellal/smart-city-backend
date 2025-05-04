package com.vimal.code.ToDo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    private String lieuName;
    private double latitude;
    private double longitude;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserEnitiy createdBy;
    @ManyToMany
    @JoinTable(
            name = "event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "citoyen_id")
    )
    private List<Citoyen> participants;
    private String description;
    @Column(nullable = true, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean validated;




}

package com.vimal.code.ToDo.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "id")

public class Agent extends UserEnitiy {

    @OneToOne
    @JoinColumn(name = "service_urbain_id") // Nom de la colonne de la clé étrangère
    private ServiceUrbain serviceUrbain; // Each Agent belongs to exactly one ServiceUrbain
}
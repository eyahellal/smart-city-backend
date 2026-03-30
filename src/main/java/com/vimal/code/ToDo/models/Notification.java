package com.vimal.code.ToDo.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEnitiy user;

    @Column(name = "is_read")
    private boolean seen;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Long reclamationId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.seen = false;
    }
}
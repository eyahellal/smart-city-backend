package com.vimal.code.ToDo.Repositories;

import com.vimal.code.ToDo.models.Citoyen;
import com.vimal.code.ToDo.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

    @Repository
    public interface EventRepository extends JpaRepository<Event, Long> {






    }
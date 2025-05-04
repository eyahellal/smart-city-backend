package com.vimal.code.ToDo.Repositories;

import com.vimal.code.ToDo.models.Citoyen;
import com.vimal.code.ToDo.models.Event;
import com.vimal.code.ToDo.models.UserEnitiy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

    @Repository
    public interface EventRepository extends JpaRepository<Event, Long> {
        List<Event> findByValidatedTrue();
        List<Event> findByValidatedFalse();


        List<Event> findByCreatedBy(UserEnitiy user);
    }
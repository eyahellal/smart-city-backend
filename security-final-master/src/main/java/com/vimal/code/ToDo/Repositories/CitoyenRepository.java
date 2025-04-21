package com.vimal.code.ToDo.Repositories;

import com.vimal.code.ToDo.models.Citoyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CitoyenRepository extends JpaRepository<Citoyen, Long> {

    @Override
    List<Citoyen> findAll();

    @Override
    Optional<Citoyen> findById(Long aLong);

    Optional<Citoyen> findByEmail(String email);



}
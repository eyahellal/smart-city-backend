package com.vimal.code.ToDo.Repositories;

import com.vimal.code.ToDo.models.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    @Override
    List<Agent> findAll();

    @Override
    Optional<Agent> findById(Long aLong);

    @Query("SELECT a FROM Agent a WHERE a.email = :email")
    Optional<Agent> findAgentByEmail(@Param("email") String email);


    Optional<Agent> findByEmail(String email);
    @Query("SELECT a.serviceUrbain.type, COUNT(a) FROM Agent a GROUP BY a.serviceUrbain.type")
    List<Object[]> countAgentsByServiceType();

}
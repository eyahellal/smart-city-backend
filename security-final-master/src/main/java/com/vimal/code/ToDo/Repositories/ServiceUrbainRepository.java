package com.vimal.code.ToDo.Repositories;


import com.vimal.code.ToDo.models.ServiceType;
import com.vimal.code.ToDo.models.ServiceUrbain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceUrbainRepository extends JpaRepository<ServiceUrbain, Long> {

    // ✅ Recherche un service urbain par son type (MAIRIE, POLICE_LOCALE, POLICE_MUNICIPALE)
    ServiceUrbain findByType(ServiceType type);


    ServiceUrbain findByType(String type);
}

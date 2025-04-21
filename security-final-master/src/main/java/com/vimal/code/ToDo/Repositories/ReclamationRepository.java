package com.vimal.code.ToDo.Repositories;

import com.vimal.code.ToDo.models.Reclamation;
import com.vimal.code.ToDo.models.ServiceType;
import com.vimal.code.ToDo.models.ServiceUrbain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
    List<Reclamation> findByResolu(boolean resolu);

    List<Reclamation> findByCitoyenId(Long citoyenId);


    List<Reclamation> findByServiceResponsable(ServiceUrbain serviceUrbain);
}




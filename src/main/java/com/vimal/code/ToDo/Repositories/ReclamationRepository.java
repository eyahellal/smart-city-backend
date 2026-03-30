package com.vimal.code.ToDo.Repositories;

import com.vimal.code.ToDo.models.Reclamation;
import com.vimal.code.ToDo.models.ServiceType;
import com.vimal.code.ToDo.models.ServiceUrbain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
    List<Reclamation> findByResolu(boolean resolu);

    List<Reclamation> findByCitoyenId(Long citoyenId);

    long countByResolu(boolean resolu);

    List<Reclamation> findByServiceResponsable(ServiceUrbain serviceUrbain);
    @Query("SELECT COUNT(r) FROM Reclamation r WHERE DATE(r.dateCreation) = :date")
    long countByDateCreation(@Param("date") LocalDate date);

    // Optional: If you want resolved reclamations per day for the trend
    @Query("SELECT COUNT(r) FROM Reclamation r WHERE r.resolu = :resolu AND DATE(r.dateCreation) = :date")
    long countByResoluAndDateCreation(@Param("resolu") boolean resolu, @Param("date") LocalDate date);
}



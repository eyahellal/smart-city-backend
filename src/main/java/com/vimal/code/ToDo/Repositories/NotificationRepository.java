package com.vimal.code.ToDo.Repositories;
import com.vimal.code.ToDo.models.Notification;
import com.vimal.code.ToDo.models.UserEnitiy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(UserEnitiy user);
    List<Notification> findByUserAndReclamationIdNotNullOrderByCreatedAtDesc(UserEnitiy user);
    long countByUserAndSeenFalse(UserEnitiy user);
}
package com.vimal.code.ToDo.service;

import com.vimal.code.ToDo.models.Notification;
import com.vimal.code.ToDo.models.Reclamation;
import com.vimal.code.ToDo.models.UserEnitiy;
import com.vimal.code.ToDo.Repositories.NotificationRepository;
import com.vimal.code.ToDo.Repositories.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class); // Add logger
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private  ReclamationService reclamationService;

    @Autowired
    private UserRepo userRepository;

    public void createReclamationNotification(Long reclamationId, String userId) {
        Optional<UserEnitiy> userOptional = userRepository.findByEmail(userId);
        UserEnitiy user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Notification notification = new Notification();
        Reclamation reclamation= reclamationService.getReclamationById(reclamationId);
        notification.setMessage("Votre réclamation  " + reclamation.getDescription() + " a été résolue.");
        notification.setUser(user);
        // No need to set seen or createdAt; @PrePersist handles them
        notification.setReclamationId(reclamationId);

        notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications(String userId) {
        UserEnitiy user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Notification> getReclamationNotifications(String userId) {
        UserEnitiy user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        logger.info("User found for userId {}: {}", userId,user); // Log the user
        return notificationRepository.findByUserAndReclamationIdNotNullOrderByCreatedAtDesc(user);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        notification.setSeen(true);
        notificationRepository.save(notification);
    }

    public long countUnreadNotifications(String userId) {
        UserEnitiy user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return notificationRepository.countByUserAndSeenFalse(user);
    }
}
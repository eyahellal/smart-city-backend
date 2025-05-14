package com.vimal.code.ToDo.controllers;

import com.vimal.code.ToDo.models.Notification;
import com.vimal.code.ToDo.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    @GetMapping("")
    public ResponseEntity<List<Notification>> getAllNotifications(@RequestParam String userId) {
        try {
            List<Notification> notifications = notificationService.getAllNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/reclamation")
    public ResponseEntity<List<Notification>> getReclamationNotifications(@RequestParam String userId) {
        try {
            List<Notification> notifications = notificationService.getReclamationNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PutMapping("/mark-read/{id}")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countUnreadNotifications(@RequestParam String userId) {
        try {
            long count = notificationService.countUnreadNotifications(userId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", Long.valueOf(e.getMessage())));
        }
    }
}
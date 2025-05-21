package com.vimal.code.ToDo.controllers;

import com.vimal.code.ToDo.dto.resp.AdminStatsDTO;
import com.vimal.code.ToDo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-stats")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<?> getAdminStats(Authentication authentication) {
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body("Access denied: Admin role required");
        }

        AdminStatsDTO stats = adminService.getAdminStats();
        return ResponseEntity.ok(stats);
    }
}
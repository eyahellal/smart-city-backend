package com.vimal.code.ToDo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SecurityTestController{
    private static final Logger logger = LoggerFactory.getLogger(SecurityTestController.class);

    @GetMapping("/auth-details")
    public ResponseEntity<AuthenticationDetails> getAuthenticationDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return ResponseEntity.status(401).body(new AuthenticationDetails("No authentication found", null, false));
        }

        AuthenticationDetails details = new AuthenticationDetails(
                authentication.getName(),
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()),
                authentication.isAuthenticated()
        );

        logger.info("Authentication Details:");
        logger.info("Principal: {}", details.getPrincipal());
        logger.info("Authorities: {}", details.getAuthorities());
        logger.info("Is Authenticated: {}", details.isAuthenticated());

        return ResponseEntity.ok(details);
    }

    @GetMapping("/check-citoyen")
    @PreAuthorize("hasRole('CITOYEN')")
    public String checkCitoyenRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logger.info("Citoyen Role Check");
        logger.info("Principal: {}", authentication.getName());
        logger.info("Authorities: {}", authentication.getAuthorities());

        return "Citoyen access granted successfully!";
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logger.info("Admin Dashboard Access");
        logger.info("Principal: {}", authentication.getName());
        logger.info("Authorities: {}", authentication.getAuthorities());

        return "Welcome to the Admin Dashboard!";
    }

    @GetMapping("/agent/dashboard")
    @PreAuthorize("hasRole('AGENT')")
    public String agentDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logger.info("Agent Dashboard Access");
        logger.info("Principal: {}", authentication.getName());
        logger.info("Authorities: {}", authentication.getAuthorities());

        return "Welcome to the Agent Dashboard!";
    }

    @GetMapping("/citoyen/dashboard")
    @PreAuthorize("hasRole('CITOYEN')")
    public String citoyenDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logger.info("Citoyen Dashboard Access");
        logger.info("Principal: {}", authentication.getName());
        logger.info("Authorities: {}", authentication.getAuthorities());

        return "Welcome to the Citoyen Dashboard!";
    }

    @GetMapping("/user/profile")
    public String userProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logger.info("User Profile Access");
        logger.info("Principal: {}", authentication.getName());
        logger.info("Authorities: {}", authentication.getAuthorities());

        return "Welcome to your Profile!";
    }

    @GetMapping("/home")
    public String home() {
        return "Welcome to the Home Page!";
    }

    // Static inner class to represent authentication details
    public static class AuthenticationDetails {
        private final String principal;
        private final Collection<String> authorities;
        private final boolean authenticated;

        public AuthenticationDetails(String principal, Collection<String> authorities, boolean authenticated) {
            this.principal = principal;
            this.authorities = authorities;
            this.authenticated = authenticated;
        }

        public String getPrincipal() {
            return principal;
        }

        public Collection<String> getAuthorities() {
            return authorities;
        }

        public boolean isAuthenticated() {
            return authenticated;
        }
    }
}
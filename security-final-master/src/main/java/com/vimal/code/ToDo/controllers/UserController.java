package com.vimal.code.ToDo.controllers;

import com.vimal.code.ToDo.dto.req.AgentRequestDto;
import com.vimal.code.ToDo.dto.req.UserRequestDto;
import com.vimal.code.ToDo.dto.resp.AgentResponseDto;
import com.vimal.code.ToDo.dto.resp.UserResponseDto;
import com.vimal.code.ToDo.service.AgentService;
import com.vimal.code.ToDo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AgentService agentService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUser() {
        return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
    }
    @PostMapping("/create-agent")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createAgent(@RequestBody AgentRequestDto agentRequestDto) {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Extract authentication details
        String authenticatedUser = authentication.getName(); // Get the username/email
        String authorities = authentication.getAuthorities().toString(); // Get roles

        // Create the agent
        AgentResponseDto createdAgent = agentService.createAgent(agentRequestDto);

        // Build response with agent details + authentication info
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Agent created successfully");
        response.put("agent", createdAgent);
        response.put("authenticatedUser", authenticatedUser);
        response.put("authorities", authorities);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/update-citoyen/{id}")
    @PreAuthorize("hasRole('ROLE_CITOYEN') or hasRole('ROLE_ADMIN')") // Only Citoyen himself or Admin can update
    public ResponseEntity<?> updateCitoyen(@PathVariable Long id, @RequestBody UserRequestDto userRequestDto) {
        try {
            UserResponseDto updatedUser = userService.updateCitoyen(id, userRequestDto);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating Citoyen with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour du citoyen: " + e.getMessage());
        }
    }





}
package com.vimal.code.ToDo.controllers;

import com.vimal.code.ToDo.Auth.JwtHelper;
import com.vimal.code.ToDo.config.AuthConfig;
import com.vimal.code.ToDo.dto.req.AgentRequestDto;
import com.vimal.code.ToDo.dto.req.JwtRequest;
import com.vimal.code.ToDo.dto.req.UserRequestDto;
import com.vimal.code.ToDo.dto.resp.JwtResponse;
import com.vimal.code.ToDo.dto.resp.UserResponseDto;
import com.vimal.code.ToDo.models.Citoyen;
import com.vimal.code.ToDo.models.Role;
import com.vimal.code.ToDo.models.UserEnitiy;
import com.vimal.code.ToDo.exp.UserAlreadyExistsException;
import com.vimal.code.ToDo.Repositories.UserRepo;
import com.vimal.code.ToDo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // Optional: allow frontend access
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JwtHelper helper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepository;

    // Register endpoint
    @PostMapping("/create")
    public ResponseEntity<JwtResponse> createUser(@RequestBody UserRequestDto userRequestDto) {
        try {
            UserResponseDto userResponseDto = userService.createUser(userRequestDto);

            UserDetails userDetails = userDetailsService.loadUserByUsername(userResponseDto.getEmail());
            String role = extractRoleFromAuthorities(userDetails.getAuthorities());

            String token = this.helper.generateToken(userDetails, role);
            Optional<UserEnitiy> optionalUser = userRepository.findByEmail(userRequestDto.getEmail());

            String city = null;
            String state = null;

            if (optionalUser.isPresent() && optionalUser.get() instanceof Citoyen citoyen) {
                city = citoyen.getCity();
                state = citoyen.getState();
            }

            JwtResponse jwtResponse = JwtResponse.builder()
                    .token(token)
                    .name(userResponseDto.getName()) // From DTO
                    .role(role)
                    .id(String.valueOf(optionalUser.get().getId()))
                    .city(city)
                    .state(state)
                    .build();

            return new ResponseEntity<>(jwtResponse, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException ex) {
            logger.warn("User already exists: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(JwtResponse.builder()
                            .token(null)
                            .name(null)
                            .role(null)
                            .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest jwtRequest) {
        this.doAuthenticate(jwtRequest.getEmail(), jwtRequest.getPassword());

        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getEmail());
        String role = extractRoleFromAuthorities(userDetails.getAuthorities());

        UserEnitiy user = userRepository.findByEmail(jwtRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + jwtRequest.getEmail()));

        String token = this.helper.generateToken(userDetails, role);
        String city = null;
        String state = null;

        if (user instanceof Citoyen) {
            Citoyen citoyen = (Citoyen) user;
            city = citoyen.getCity();
            state = citoyen.getState();
        }

        JwtResponse jwtResponse = JwtResponse.builder()
                .token(token)
                .name(user.getName())
                .id(String.valueOf(user.getId()))
                .role(role)
                .city(city)
                .state(state)
                .build();

        return ResponseEntity.ok(jwtResponse);
    }

    // Helper: Extract role
    private String extractRoleFromAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            return "CITOYEN"; // Default role
        }

        String authority = authorities.iterator().next().getAuthority();
        return authority.startsWith("ROLE_") ? authority.substring(5) : authority;
    }

    // Helper: Authenticate credentials
    private void doAuthenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, password);

        try {
            manager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Authentication successful for user: {}", email);
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for user: {}", email);
            throw new BadCredentialsException("Invalid username or password");
        }
    }
    // Handle bad credentials
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password!");
    }

    @PostMapping("/admin/create-agent")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // Restrict to admins
    public ResponseEntity<JwtResponse> createAgent(@RequestBody AgentRequestDto agentRequestDto) {
        try {
            // Force the role to agent (optional, since createAgent sets it)
            agentRequestDto.setRole(Role.AGENT);
            logger.info("Admin creating agent with email: {} and role: {}", agentRequestDto.getEmail(), agentRequestDto.getRole());

            // Create agent via service
            UserResponseDto userResponseDto = userService.createAgent(agentRequestDto);

            // Load user details for token generation
            UserDetails userDetails = userDetailsService.loadUserByUsername(userResponseDto.getEmail());
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            logger.info("Authorities after creation: {}", authorities);

            String role = extractRoleFromAuthorities(authorities);
            if (!"AGENT".equals(role)) {
                logger.warn("Role mismatch: Expected AGENT, got {}", role);
                throw new IllegalStateException("Role not set to AGENT after creation");
            }

            // Generate JWT token
            String token = helper.generateToken(userDetails, role);

            // Load full User entity for additional fields
            Optional<UserEnitiy> optionalUser = userRepository.findByEmail(agentRequestDto.getEmail());
            if (!optionalUser.isPresent()) {
                logger.error("User entity not found after creation for email: {}", agentRequestDto.getEmail());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(JwtResponse.builder()
                                .token(null)
                                .name(null)
                                .role(null)
                                .build());
            }

            // Build response
            JwtResponse jwtResponse = JwtResponse.builder()
                    .token(token)
                    .name(userResponseDto.getName())
                    .id(String.valueOf(optionalUser.get().getId()))
                    .role(role)
                    .build();

            logger.info("Agent created successfully with email: {}", agentRequestDto.getEmail());
            return new ResponseEntity<>(jwtResponse, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException ex) {
            logger.warn("Agent already exists: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(JwtResponse.builder()
                            .token(null)
                            .name(null)
                            .role(null)
                            .build());
        } catch (IllegalStateException ex) {
            logger.error("Role configuration error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(JwtResponse.builder()
                            .token(null)
                            .name(null)
                            .role(null)
                            .build());
        } catch (Exception ex) {
            logger.error("Error creating agent: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(JwtResponse.builder()
                            .token(null)
                            .name(null)
                            .role(null)
                            .build());
        }
    }}
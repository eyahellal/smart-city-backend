package com.vimal.code.ToDo.config;

import com.vimal.code.ToDo.Auth.JWTAuthenticationEntryPoint;
import com.vimal.code.ToDo.Auth.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // ✅ Required for @PreAuthorize to work
public class SecurityConfig {

    @Autowired
    private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Autowired
    private UserDetailsService userDetailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ Apply proper CORS settings
                .authorizeHttpRequests(authorize -> authorize
                        // Public Endpoints (No Authentication Needed)
                        .requestMatchers("/auth/login", "/auth/create","/error","/api/geo/search").permitAll()

                        // ✅ Role-Based Protected Endpoints
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")  // Admin Only
                        .requestMatchers("/agent/**").hasAuthority("ROLE_AGENT")  // Agent Only
                        .requestMatchers("/citoyen/**","api/images/**").hasAuthority("ROLE_CITOYEN")  // Citoyen Only
                        .requestMatchers("/reclamations/agent/getAll").hasAuthority("ROLE_AGENT")
                        .requestMatchers("/Events/**").hasAuthority("ROLE_AGENT")  // Agent Only


                        .requestMatchers("/reclamation/**").hasAuthority("ROLE_CITOYEN")  // Citoyen Only
                        .requestMatchers("/Events/**").hasAuthority("ROLE_CITOYEN")  // Agent Only
                        .requestMatchers("/api/geo/search").hasAuthority("ROLE_CITOYEN")  // Agent Only
                        .requestMatchers("/user/update-citoyen/**").hasAuthority("ROLE_CITOYEN")  // Agent Only





                        .requestMatchers("/user/admin/**").hasAuthority("ROLE_ADMIN")  // Admin Only

                        // Any other request must be authenticated
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider doDaoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    // ✅ CORS Configuration (Allows React Frontend to Call API)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173")); // Adjust to match frontend URL
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

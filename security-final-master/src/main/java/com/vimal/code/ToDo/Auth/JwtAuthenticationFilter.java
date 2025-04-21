package com.vimal.code.ToDo.Auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    private final List<String> publicEndpoints = List.of(
            "/auth/login",
            "/auth/create"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getServletPath();

        // ✅ Log request path for debugging
        logger.debug("⚙️ Processing request path: {}", requestPath);

        // ✅ Skip JWT validation for public endpoints
        if (publicEndpoints.stream().anyMatch(endpoint ->
                requestPath.equals(endpoint) ||
                        (endpoint.endsWith("/") && requestPath.startsWith(endpoint)))) {
            logger.debug("🔓 Skipping JWT validation for public endpoint: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Log incoming request
        logger.info("Incoming request: {}", request.getRequestURI());

        // ✅ Extract the Authorization header
        String requestHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            token = requestHeader.substring(7);
            try {
                username = jwtHelper.getUsernameFromToken(token);
            } catch (ExpiredJwtException e) {
                logger.error("❌ Expired JWT token!");
            } catch (MalformedJwtException e) {
                logger.error("❌ Invalid JWT token!");
            } catch (IllegalArgumentException e) {
                logger.error("❌ Error extracting username from token!");
            }
        } else {
            logger.warn("⚠️ No valid Authorization header found!");
        }

        // ✅ Validate token and authenticate user
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtHelper.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.info("✅ User '{}' authenticated successfully with roles: {}", username, userDetails.getAuthorities());
            } else {
                logger.warn("❌ Token validation failed for user: {}", username);
            }
        }

        // ✅ Log authentication state
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logger.info("🔍 SecurityContext Authentication: User = {}, Roles = {}", auth.getName(), auth.getAuthorities());
        } else {
            logger.warn("🚨 No authentication found in SecurityContext!");
        }

        // ✅ Continue request processing
        filterChain.doFilter(request, response);
    }
}

package com.library.user.infrastructure.filter;

import com.library.user.application.service.UserContextService;
import com.library.user.application.service.UserContextService.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to extract user context from API Gateway headers
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class UserContextFilter extends OncePerRequestFilter {
    
    private final UserContextService userContextService;
    
    // Header names from API Gateway
    private static final String X_USER_ID = "X-User-Id";
    private static final String X_USERNAME = "X-Username";
    private static final String X_USER_EMAIL = "X-User-Email";
    private static final String X_USER_ROLES = "X-User-Roles";
    private static final String X_USER_PERMISSIONS = "X-User-Permissions";
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Extract user context from headers
            String keycloakId = request.getHeader(X_USER_ID);
            String username = request.getHeader(X_USERNAME);
            String email = request.getHeader(X_USER_EMAIL);
            String roles = request.getHeader(X_USER_ROLES);
            String permissions = request.getHeader(X_USER_PERMISSIONS);
            
            log.debug("Processing request with user context - Keycloak ID: {}, Username: {}", 
                keycloakId, username);
            
            if (keycloakId != null && !keycloakId.trim().isEmpty()) {
                // Get user context
                UserContext userContext = userContextService.getCurrentUserContext(
                    keycloakId, username, email, roles, permissions);
                
                if (userContext != null) {
                    // Store user context in request attribute
                    request.setAttribute("userContext", userContext);
                    log.debug("User context set for request: {}", userContext.getUsername());
                } else {
                    log.warn("Failed to create user context for Keycloak ID: {}", keycloakId);
                }
            } else {
                log.debug("No user context headers found in request");
            }
            
        } catch (Exception e) {
            log.error("Error processing user context from headers", e);
            // Continue with request even if user context processing fails
        }
        
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip filter for health check and public endpoints
        return path.startsWith("/actuator/") || 
               path.startsWith("/api/auth/public/");
    }
}
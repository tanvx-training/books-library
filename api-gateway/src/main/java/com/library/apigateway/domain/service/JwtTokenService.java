package com.library.apigateway.domain.service;

import com.library.apigateway.domain.model.UserContext;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Domain service for JWT token operations
 */
public interface JwtTokenService {
    
    /**
     * Extract user context from JWT token
     */
    UserContext extractUserContext(Jwt jwt);
    
    /**
     * Validate JWT token
     */
    boolean isTokenValid(String token);
    
    /**
     * Extract keycloak user ID from JWT
     */
    String extractKeycloakId(Jwt jwt);
    
    /**
     * Extract username from JWT
     */
    String extractUsername(Jwt jwt);
    
    /**
     * Extract user roles from JWT
     */
    java.util.Set<String> extractRoles(Jwt jwt);
}
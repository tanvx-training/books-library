package com.library.book.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class SecurityUtils {

    /**
     * Get the current authenticated user's ID (subject from JWT)
     * @return the user ID or null if not authenticated
     */
    public String getCurrentUserId() {
        return getJwt().map(Jwt::getSubject).orElse(null);
    }
    
    /**
     * Get the current authenticated user's username
     * @return the username or null if not authenticated
     */
    public String getCurrentUsername() {
        return getJwt().map(jwt -> jwt.getClaimAsString("preferred_username")).orElse(null);
    }
    
    /**
     * Get the current authenticated user's email
     * @return the email or null if not authenticated
     */
    public String getCurrentUserEmail() {
        return getJwt().map(jwt -> jwt.getClaimAsString("email")).orElse(null);
    }
    
    /**
     * Check if the current user has a specific role
     * @param role the role to check (without the ROLE_ prefix)
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        List<String> roles = getCurrentUserRoles();
        return roles.contains(role.toUpperCase());
    }
    
    /**
     * Get the current authenticated user's roles from the JWT
     * @return list of roles or empty list if not authenticated
     */
    public List<String> getCurrentUserRoles() {
        return getJwt().map(this::extractRoles).orElse(Collections.emptyList());
    }
    
    /**
     * Extract roles from the JWT token
     * @param jwt the JWT token
     * @return list of roles
     */
    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            return (List<String>) realmAccess.get("roles");
        }
        return Collections.emptyList();
    }
    
    /**
     * Get the current JWT token
     * @return Optional containing the JWT token or empty if not authenticated
     */
    public Optional<Jwt> getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            return Optional.of(((JwtAuthenticationToken) authentication).getToken());
        }
        return Optional.empty();
    }
}
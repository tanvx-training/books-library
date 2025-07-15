package com.library.book.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Service for managing user context from API Gateway headers
 */
@Slf4j
@Service
public class UserContextService {
    
    /**
     * Get user context from headers provided by API Gateway
     */
    public UserContext getCurrentUserContext(
            String keycloakId,
            String username,
            String email,
            String rolesHeader,
            String permissionsHeader) {
        
        log.debug("Getting user context for Keycloak ID: {}", keycloakId);
        
        if (keycloakId == null || keycloakId.trim().isEmpty()) {
            log.warn("No Keycloak ID provided in headers");
            return null;
        }
        
        try {
            // Parse roles and permissions from headers
            Set<String> roles = parseCommaSeparatedValues(rolesHeader);
            Set<String> permissions = parseCommaSeparatedValues(permissionsHeader);
            
            return UserContext.builder()
                .keycloakId(keycloakId)
                .username(username)
                .email(email)
                .roles(roles)
                .permissions(permissions)
                .build();
            
        } catch (Exception e) {
            log.error("Error getting user context for Keycloak ID: {}", keycloakId, e);
            return null;
        }
    }
    
    /**
     * Check if current user has specific role
     */
    public boolean hasRole(UserContext userContext, String role) {
        return userContext != null && 
               userContext.getRoles() != null && 
               userContext.getRoles().contains(role);
    }
    
    /**
     * Check if current user has any of the specified roles
     */
    public boolean hasAnyRole(UserContext userContext, String... roles) {
        if (userContext == null || userContext.getRoles() == null) {
            return false;
        }
        
        for (String role : roles) {
            if (userContext.getRoles().contains(role)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if current user has specific permission
     */
    public boolean hasPermission(UserContext userContext, String permission) {
        return userContext != null && 
               userContext.getPermissions() != null && 
               userContext.getPermissions().contains(permission);
    }
    
    private Set<String> parseCommaSeparatedValues(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Set.of();
        }
        
        return Set.of(value.split(","));
    }
    
    /**
     * User context data class
     */
    @lombok.Builder
    @lombok.Data
    public static class UserContext {
        private String keycloakId;
        private String username;
        private String email;
        private Set<String> roles;
        private Set<String> permissions;
        
        public boolean hasRole(String role) {
            return roles != null && roles.contains(role);
        }
        
        public boolean hasPermission(String permission) {
            return permissions != null && permissions.contains(permission);
        }
        
        public boolean isAdmin() {
            return hasRole("ADMIN");
        }
        
        public boolean isLibrarian() {
            return hasRole("LIBRARIAN");
        }
        
        public boolean isUser() {
            return hasRole("USER");
        }
        
        public boolean canManageBooks() {
            return isAdmin() || isLibrarian();
        }
        
        public boolean canBorrowBooks() {
            return isUser() || isLibrarian() || isAdmin();
        }
    }
}
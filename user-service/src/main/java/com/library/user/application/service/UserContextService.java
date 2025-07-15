package com.library.user.application.service;

import com.library.user.domain.model.user.KeycloakId;
import com.library.user.domain.model.user.User;
import com.library.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * Service for managing user context from API Gateway headers
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserContextService {
    
    private final UserRepository userRepository;
    private final KeycloakUserSyncService keycloakUserSyncService;
    
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
            // Try to get user from local database
            Optional<User> userOpt = userRepository.findByKeycloakId(KeycloakId.of(keycloakId));
            
            User user;
            if (userOpt.isPresent()) {
                user = userOpt.get();
            } else {
                // User not found locally, sync from Keycloak
                log.info("User not found locally, syncing from Keycloak: {}", keycloakId);
                user = keycloakUserSyncService.syncUserFromKeycloak(keycloakId);
                
                if (user == null) {
                    log.warn("Failed to sync user from Keycloak: {}", keycloakId);
                    return null;
                }
            }
            
            // Parse roles and permissions from headers
            Set<String> roles = parseCommaSeparatedValues(rolesHeader);
            Set<String> permissions = parseCommaSeparatedValues(permissionsHeader);
            
            return UserContext.builder()
                .user(user)
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
     * Get user by Keycloak ID
     */
    public Optional<User> getUserByKeycloakId(String keycloakId) {
        if (keycloakId == null || keycloakId.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return userRepository.findByKeycloakId(KeycloakId.of(keycloakId));
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
        private User user;
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
    }
}
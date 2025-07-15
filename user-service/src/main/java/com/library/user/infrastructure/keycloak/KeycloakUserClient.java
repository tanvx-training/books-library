package com.library.user.infrastructure.keycloak;

import com.library.user.infrastructure.keycloak.dto.KeycloakUserDto;

import java.util.List;

/**
 * Client interface for Keycloak user operations
 */
public interface KeycloakUserClient {
    
    /**
     * Get user by Keycloak ID
     */
    KeycloakUserDto getUserById(String keycloakId);
    
    /**
     * Get user by username
     */
    KeycloakUserDto getUserByUsername(String username);
    
    /**
     * Get user by email
     */
    KeycloakUserDto getUserByEmail(String email);
    
    /**
     * Get all users
     */
    List<KeycloakUserDto> getAllUsers();
    
    /**
     * Create new user in Keycloak
     */
    String createUser(KeycloakUserDto userDto);
    
    /**
     * Update user in Keycloak
     */
    void updateUser(String keycloakId, KeycloakUserDto userDto);
    
    /**
     * Delete user from Keycloak
     */
    void deleteUser(String keycloakId);
    
    /**
     * Enable/disable user in Keycloak
     */
    void setUserEnabled(String keycloakId, boolean enabled);
    
    /**
     * Get user roles
     */
    List<String> getUserRoles(String keycloakId);
    
    /**
     * Assign role to user
     */
    void assignRoleToUser(String keycloakId, String roleName);
    
    /**
     * Remove role from user
     */
    void removeRoleFromUser(String keycloakId, String roleName);
}
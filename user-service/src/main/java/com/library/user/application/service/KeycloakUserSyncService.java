package com.library.user.application.service;

import com.library.user.domain.model.user.*;
import com.library.user.domain.repository.UserRepository;
import com.library.user.infrastructure.keycloak.KeycloakUserClient;
import com.library.user.infrastructure.keycloak.dto.KeycloakUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Application service for synchronizing users between Keycloak and local database
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakUserSyncService {
    
    private final UserRepository userRepository;
    private final KeycloakUserClient keycloakUserClient;
    
    /**
     * Sync user from Keycloak to local database
     */
    @Transactional
    public User syncUserFromKeycloak(String keycloakId) {
        log.debug("Syncing user from Keycloak: {}", keycloakId);
        
        try {
            // Get user from Keycloak
            KeycloakUserDto keycloakUser = keycloakUserClient.getUserById(keycloakId);
            if (keycloakUser == null) {
                log.warn("User not found in Keycloak: {}", keycloakId);
                return null;
            }
            
            // Check if user already exists in local database
            Optional<User> existingUser = userRepository.findByKeycloakId(KeycloakId.of(keycloakId));
            
            if (existingUser.isPresent()) {
                // Update existing user
                return updateExistingUser(existingUser.get(), keycloakUser);
            } else {
                // Create new user
                return createNewUserFromKeycloak(keycloakUser);
            }
            
        } catch (Exception e) {
            log.error("Error syncing user from Keycloak: {}", keycloakId, e);
            throw new RuntimeException("Failed to sync user from Keycloak", e);
        }
    }
    
    /**
     * Sync user to Keycloak (when user is created locally first)
     */
    @Transactional
    public void syncUserToKeycloak(User user) {
        log.debug("Syncing user to Keycloak: {}", user.getUsername().getValue());
        
        try {
            KeycloakUserDto keycloakUserDto = KeycloakUserDto.builder()
                .username(user.getUsername().getValue())
                .email(user.getEmail().getValue())
                .firstName(user.getFirstName().getValue())
                .lastName(user.getLastName().getValue())
                .enabled(user.isActive())
                .build();
            
            String keycloakId = keycloakUserClient.createUser(keycloakUserDto);
            
            // Update user with Keycloak ID
            user.setKeycloakId(KeycloakId.of(keycloakId));
            userRepository.save(user);
            
            log.info("User synced to Keycloak successfully: {} -> {}", 
                user.getUsername().getValue(), keycloakId);
            
        } catch (Exception e) {
            log.error("Error syncing user to Keycloak: {}", user.getUsername().getValue(), e);
            throw new RuntimeException("Failed to sync user to Keycloak", e);
        }
    }
    
    /**
     * Sync all users from Keycloak
     */
    @Transactional
    public void syncAllUsersFromKeycloak() {
        log.info("Starting full user sync from Keycloak");
        
        try {
            var keycloakUsers = keycloakUserClient.getAllUsers();
            
            for (KeycloakUserDto keycloakUser : keycloakUsers) {
                try {
                    syncUserFromKeycloak(keycloakUser.getId());
                } catch (Exception e) {
                    log.error("Error syncing user: {}", keycloakUser.getId(), e);
                    // Continue with other users
                }
            }
            
            log.info("Completed full user sync from Keycloak. Processed {} users", keycloakUsers.size());
            
        } catch (Exception e) {
            log.error("Error during full user sync from Keycloak", e);
            throw new RuntimeException("Failed to sync all users from Keycloak", e);
        }
    }
    
    private User updateExistingUser(User existingUser, KeycloakUserDto keycloakUser) {
        log.debug("Updating existing user: {}", existingUser.getUsername().getValue());
        
        // Update user information from Keycloak
        existingUser.setUsername(Username.of(keycloakUser.getUsername()));
        existingUser.setEmail(Email.of(keycloakUser.getEmail()));
        existingUser.setFirstName(FirstName.of(keycloakUser.getFirstName()));
        existingUser.setLastName(LastName.of(keycloakUser.getLastName()));
        
        return userRepository.save(existingUser);
    }
    
    private User createNewUserFromKeycloak(KeycloakUserDto keycloakUser) {
        log.debug("Creating new user from Keycloak: {}", keycloakUser.getUsername());
        
        User newUser = User.createWithKeycloak(
            KeycloakId.of(keycloakUser.getId()),
            Username.of(keycloakUser.getUsername()),
            Email.of(keycloakUser.getEmail()),
            FirstName.of(keycloakUser.getFirstName()),
            LastName.of(keycloakUser.getLastName()),
            null // Phone number not available from Keycloak
        );
        
        return userRepository.save(newUser);
    }
}
package com.library.user.application.service;

import com.library.user.domain.model.user.*;
import com.library.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service to synchronize user data between Keycloak and our database
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final UserRepository userRepository;

    /**
     * Synchronizes the current user from the JWT token with our database
     * If the user doesn't exist, it will be created (JIT provisioning)
     *
     * @return The synchronized user
     */
    @Transactional
    public User syncCurrentUser() {
        // Get the JWT token from the security context
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Extract user information from the JWT
        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String username = jwt.getClaimAsString("preferred_username");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        
        log.debug("Synchronizing user from Keycloak: {}", keycloakId);
        
        // Check if user already exists in our database
        Optional<User> existingUser = userRepository.findByKeycloakId(KeycloakId.of(keycloakId));
        
        if (existingUser.isPresent()) {
            log.debug("User already exists in database, updating information");
            User user = existingUser.get();
            
            // Update user information if needed
            boolean updated = false;
            
            if (!user.getEmail().getValue().equals(email)) {
                user.setEmail(new Email(email));
                updated = true;
            }
            
            if (!user.getUsername().getValue().equals(username)) {
                user.setUsername(new Username(username));
                updated = true;
            }
            
            if ((user.getFirstName() == null && firstName != null) || 
                (user.getFirstName() != null && !user.getFirstName().getValue().equals(firstName))) {
                user.setFirstName(new FirstName(firstName));
                updated = true;
            }
            
            if ((user.getLastName() == null && lastName != null) || 
                (user.getLastName() != null && !user.getLastName().getValue().equals(lastName))) {
                user.setLastName(new LastName(lastName));
                updated = true;
            }
            
            if (updated) {
                log.info("User information updated from Keycloak: {}", keycloakId);
                return userRepository.save(user);
            }
            
            return user;
        } else {
            log.info("Creating new user from Keycloak: {}", keycloakId);
            
            // Create new user
            User newUser = User.createWithKeycloak(
                    KeycloakId.of(keycloakId),
                    new Username(username),
                    new Email(email),
                    firstName != null ? new FirstName(firstName) : null,
                    lastName != null ? new LastName(lastName) : null,
                    null // No phone number from Keycloak
            );
            
            return userRepository.save(newUser);
        }
    }
} 
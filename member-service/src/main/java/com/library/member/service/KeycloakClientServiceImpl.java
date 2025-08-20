package com.library.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakClientServiceImpl implements KeycloakClientService {

    private final Keycloak keycloakClient;

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public List<UserRepresentation> getAllUsers() {
        try {
            List<UserRepresentation> users = keycloakClient.realm(realm)
                    .users()
                    .list();
            
            log.info("Retrieved {} users from Keycloak", users.size());
            return users;
        } catch (Exception e) {
            log.error("Failed to retrieve users from Keycloak", e);
            throw new RuntimeException("Failed to retrieve users from Keycloak", e);
        }
    }

    @Override
    public List<UserRepresentation> getUpdatedUsers(Instant since) {
        try {
            List<UserRepresentation> allUsers = getAllUsers();
            
            return allUsers.stream()
                    .filter(user -> isUserUpdatedSince(user, since))
                    .toList();
        } catch (Exception e) {
            log.error("Failed to retrieve updated users from Keycloak since {}", since, e);
            throw new RuntimeException("Failed to retrieve updated users from Keycloak", e);
        }
    }

    @Override
    public Optional<UserRepresentation> getUserById(String keycloakId) {
        try {
            UserRepresentation user = keycloakClient.realm(realm)
                    .users()
                    .get(keycloakId)
                    .toRepresentation();
            
            return Optional.of(user);
        } catch (Exception e) {
            log.warn("User not found in Keycloak: {}", keycloakId);
            return Optional.empty();
        }
    }

    @Override
    public List<String> getAllUserIds() {
        try {
            return getAllUsers().stream()
                    .map(UserRepresentation::getId)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to retrieve user IDs from Keycloak", e);
            throw new RuntimeException("Failed to retrieve user IDs from Keycloak", e);
        }
    }

    private boolean isUserUpdatedSince(UserRepresentation user, Instant since) {
        if (user.getCreatedTimestamp() != null && user.getCreatedTimestamp() > since.toEpochMilli()) {
            return true;
        }
        
        if (user.getAttributes() != null && user.getAttributes().containsKey("lastModified")) {
            try {
                List<String> lastModifiedList = user.getAttributes().get("lastModified");
                if (!lastModifiedList.isEmpty()) {
                    long lastModified = Long.parseLong(lastModifiedList.get(0));
                    return lastModified > since.toEpochMilli();
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid lastModified timestamp for user {}: {}", user.getId(), e.getMessage());
            }
        }
        
        return false;
    }
}
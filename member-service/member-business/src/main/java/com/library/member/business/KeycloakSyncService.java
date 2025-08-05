package com.library.member.business;

import com.library.member.business.dto.sync.SyncResult;
import com.library.member.business.mapper.UserMapper;
import com.library.member.repository.SyncStateRepository;
import com.library.member.repository.UserRepository;
import com.library.member.repository.entity.SyncStateEntity;
import com.library.member.repository.entity.UserEntity;
import com.library.member.repository.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakSyncService {

    private final KeycloakClientService keycloakClient;
    private final UserRepository userRepository;
    private final SyncStateRepository syncStateRepository;
    private final UserMapper userMapper;

    @Transactional
    public SyncResult syncAllUsers() {
        log.info("Starting full user synchronization");
        SyncResult result = new SyncResult();

        try {
            List<UserRepresentation> keycloakUsers = keycloakClient.getAllUsers();
            log.info("Found {} users in Keycloak", keycloakUsers.size());

            for (UserRepresentation kcUser : keycloakUsers) {
                try {
                    syncSingleUser(kcUser);
                    result.incrementSuccess();
                } catch (Exception e) {
                    log.error("Failed to sync user: {}", kcUser.getId(), e);
                    result.incrementFailure();
                    result.addError(String.format("User %s: %s", kcUser.getId(), e.getMessage()));
                }
            }

            saveSyncState(result);
            result.complete();

            log.info("User synchronization completed - Success: {}, Failed: {}, Total: {}",
                    result.getSuccessCount(), result.getFailureCount(), result.getTotalCount());

        } catch (Exception e) {
            log.error("User synchronization failed", e);
            result.addError("Sync failed: " + e.getMessage());
            result.complete();
            throw new RuntimeException("User synchronization failed", e);
        }

        return result;
    }

    @Transactional
    public void syncUserById(String keycloakId) {
        log.info("Syncing single user: {}", keycloakId);

        keycloakClient.getUserById(keycloakId)
                .ifPresentOrElse(
                        this::syncSingleUser,
                        () -> {
                            log.warn("User not found in Keycloak, deactivating: {}", keycloakId);
                            deactivateUser(keycloakId);
                        }
                );
    }

    @Transactional
    public void deactivateUser(String keycloakId) {
        userRepository.findByKeycloakId(keycloakId)
                .ifPresent(user -> {
                    user.setIsActive(false);
                    user.setDeletedAt(LocalDateTime.now());
                    user.setUpdatedBy("SYSTEM");
                    user.setUpdatedAt(LocalDateTime.now());
                    userRepository.save(user);
                    log.info("Deactivated user: {}", keycloakId);
                });
    }

    private void syncSingleUser(UserRepresentation kcUser) {
        UserEntity existingUser = userRepository.findByKeycloakId(kcUser.getId())
                .orElse(null);

        if (existingUser != null) {
            updateExistingUser(existingUser, kcUser);
        } else {
            createNewUser(kcUser);
        }
    }

    private void createNewUser(UserRepresentation kcUser) {
        try {
            UserEntity newUser = UserEntity.builder()
                    .keycloakId(kcUser.getId())
                    .username(kcUser.getUsername())
                    .email(kcUser.getEmail())
                    .firstName(kcUser.getFirstName())
                    .lastName(kcUser.getLastName())
                    .isActive(kcUser.isEnabled())
                    .role(extractUserRole(kcUser))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .createdBy("SYSTEM")
                    .updatedBy("SYSTEM")
                    .build();

            userRepository.save(newUser);
            log.info("Created new user: {} ({})", kcUser.getId(), kcUser.getEmail());

        } catch (Exception e) {
            log.error("Failed to create user: {}", kcUser.getId(), e);
            throw new RuntimeException("Failed to create user: " + kcUser.getId(), e);
        }
    }

    private void updateExistingUser(UserEntity user, UserRepresentation kcUser) {
        boolean updated = false;

        if (!Objects.equals(user.getEmail(), kcUser.getEmail())) {
            user.setEmail(kcUser.getEmail());
            updated = true;
        }

        if (!Objects.equals(user.getFirstName(), kcUser.getFirstName())) {
            user.setFirstName(kcUser.getFirstName());
            updated = true;
        }

        if (!Objects.equals(user.getLastName(), kcUser.getLastName())) {
            user.setLastName(kcUser.getLastName());
            updated = true;
        }

        if (!Objects.equals(user.getUsername(), kcUser.getUsername())) {
            user.setUsername(kcUser.getUsername());
            updated = true;
        }

        if (user.getIsActive() != kcUser.isEnabled()) {
            user.setIsActive(kcUser.isEnabled());
            if (!kcUser.isEnabled()) {
                user.setDeletedAt(LocalDateTime.now());
            } else {
                user.setDeletedAt(null);
            }
            updated = true;
        }

        UserRole newRole = extractUserRole(kcUser);
        if (!Objects.equals(user.getRole(), newRole)) {
            user.setRole(newRole);
            updated = true;
        }

        if (updated) {
            user.setUpdatedAt(LocalDateTime.now());
            user.setUpdatedBy("SYSTEM");
            userRepository.save(user);
            log.info("Updated user: {} ({})", kcUser.getId(), kcUser.getEmail());
        }
    }

    private UserRole extractUserRole(UserRepresentation kcUser) {
        Map<String, List<String>> attributes = kcUser.getAttributes();
        
        if (attributes != null && attributes.containsKey("roles")) {
            List<String> roles = attributes.get("roles");
            if (roles != null && !roles.isEmpty()) {
                String roleStr = roles.get(0).toUpperCase();
                try {
                    return UserRole.valueOf(roleStr);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid role '{}' for user {}, defaulting to USER", roleStr, kcUser.getId());
                }
            }
        }

        if (kcUser.getRealmRoles() != null) {
            Set<String> realmRoles = Set.of(kcUser.getRealmRoles().toArray(new String[0]));
            if (realmRoles.contains("ADMIN")) {
                return UserRole.ADMIN;
            } else if (realmRoles.contains("LIBRARIAN")) {
                return UserRole.LIBRARIAN;
            }
        }

        return UserRole.MEMBER;
    }

    private void saveSyncState(SyncResult result) {
        try {
            SyncStateEntity syncState = SyncStateEntity.builder()
                    .syncType("SCHEDULED")
                    .lastSyncTime(result.getStartTime())
                    .syncedUserCount(result.getTotalCount())
                    .successCount(result.getSuccessCount())
                    .failureCount(result.getFailureCount())
                    .createdAt(LocalDateTime.now())
                    .build();

            syncStateRepository.save(syncState);
            log.info("Saved sync state: {} users processed", result.getTotalCount());

        } catch (Exception e) {
            log.error("Failed to save sync state", e);
        }
    }
}
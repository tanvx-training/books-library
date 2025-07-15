package com.library.user.interfaces.rest;

import com.library.user.application.service.KeycloakUserSyncService;
import com.library.user.application.service.UserContextService;
import com.library.user.application.service.UserContextService.UserContext;
import com.library.user.domain.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * REST Controller for authentication and user context operations
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserContextService userContextService;
    private final KeycloakUserSyncService keycloakUserSyncService;
    
    /**
     * Get current user information from context
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser(HttpServletRequest request) {
        log.debug("Getting current user information");
        
        UserContext userContext = (UserContext) request.getAttribute("userContext");
        if (userContext == null) {
            log.warn("No user context found in request");
            return ResponseEntity.status(401).build();
        }
        
        User user = userContext.getUser();
        UserInfoResponse response = UserInfoResponse.builder()
            .keycloakId(userContext.getKeycloakId())
            .username(userContext.getUsername())
            .email(userContext.getEmail())
            .firstName(user.getFirstName() != null ? user.getFirstName().getValue() : null)
            .lastName(user.getLastName() != null ? user.getLastName().getValue() : null)
            .phone(user.getPhone() != null ? user.getPhone().getValue() : null)
            .roles(userContext.getRoles())
            .permissions(userContext.getPermissions())
            .isActive(user.isActive())
            .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Sync user from Keycloak
     */
    @PostMapping("/sync/{keycloakId}")
    public ResponseEntity<Map<String, String>> syncUser(@PathVariable String keycloakId) {
        log.info("Syncing user from Keycloak: {}", keycloakId);
        
        try {
            User user = keycloakUserSyncService.syncUserFromKeycloak(keycloakId);
            if (user != null) {
                return ResponseEntity.ok(Map.of(
                    "message", "User synced successfully",
                    "keycloakId", keycloakId,
                    "username", user.getUsername().getValue()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error syncing user: {}", keycloakId, e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to sync user",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Sync all users from Keycloak
     */
    @PostMapping("/sync/all")
    public ResponseEntity<Map<String, String>> syncAllUsers() {
        log.info("Starting full user sync from Keycloak");
        
        try {
            keycloakUserSyncService.syncAllUsersFromKeycloak();
            return ResponseEntity.ok(Map.of(
                "message", "All users synced successfully"
            ));
        } catch (Exception e) {
            log.error("Error syncing all users", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to sync all users",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Check user permissions
     */
    @GetMapping("/permissions")
    public ResponseEntity<PermissionCheckResponse> checkPermissions(
            HttpServletRequest request,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String permission) {
        
        UserContext userContext = (UserContext) request.getAttribute("userContext");
        if (userContext == null) {
            return ResponseEntity.status(401).build();
        }
        
        boolean hasRole = role != null ? userContext.hasRole(role) : true;
        boolean hasPermission = permission != null ? userContext.hasPermission(permission) : true;
        
        PermissionCheckResponse response = PermissionCheckResponse.builder()
            .hasRole(hasRole)
            .hasPermission(hasPermission)
            .roles(userContext.getRoles())
            .permissions(userContext.getPermissions())
            .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "user-service-auth"
        ));
    }
    
    @lombok.Builder
    @lombok.Data
    public static class UserInfoResponse {
        private String keycloakId;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private java.util.Set<String> roles;
        private java.util.Set<String> permissions;
        private boolean isActive;
    }
    
    @lombok.Builder
    @lombok.Data
    public static class PermissionCheckResponse {
        private boolean hasRole;
        private boolean hasPermission;
        private java.util.Set<String> roles;
        private java.util.Set<String> permissions;
    }
}
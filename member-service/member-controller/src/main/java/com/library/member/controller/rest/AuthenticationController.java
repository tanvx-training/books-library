package com.library.member.controller.rest;

import com.library.member.business.dto.response.PermissionsResponse;
import com.library.member.business.security.AuthenticatedUser;
import com.library.member.business.security.UnifiedAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/members/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UnifiedAuthenticationService authenticationService;

    @GetMapping("/permissions")
    public ResponseEntity<PermissionsResponse> getUserPermissions() {
        AuthenticatedUser currentUser = authenticationService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Build permissions response
        PermissionsResponse permissions = PermissionsResponse.builder()
                .keycloakId(currentUser.getKeycloakId())
                .username(currentUser.getUsername())
                .roles(currentUser.getRoles() != null ? currentUser.getRoles() : Set.of())
                .isAdmin(currentUser.isAdmin())
                .isLibrarian(currentUser.isLibrarian())
                .isAuthenticated(true)
                .permissions(buildPermissions(currentUser))
                .build();
        return ResponseEntity.ok(permissions);
    }

    private Set<String> buildPermissions(AuthenticatedUser user) {
        Set<String> permissions = Set.of("READ_OWN_PROFILE");
        
        if (user.isAdmin()) {
            permissions = Set.of(
                "READ_OWN_PROFILE",
                "READ_ALL_USERS",
                "MANAGE_USERS",
                "MANAGE_LIBRARY_CARDS",
                "ADMIN_ACCESS"
            );
        } else if (user.isLibrarian()) {
            permissions = Set.of(
                "READ_OWN_PROFILE",
                "READ_ALL_USERS",
                "MANAGE_LIBRARY_CARDS",
                "LIBRARIAN_ACCESS"
            );
        }
        
        return permissions;
    }
}
package com.library.notification.dto.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class AuthenticatedUser {

    private String keycloakId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles;
    private Map<String, Object> customClaims;

    public UUID getPublicId() {
        return keycloakId != null ? UUID.fromString(keycloakId) : null;
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public boolean hasAnyRole(String... roles) {
        if (this.roles == null || roles == null) {
            return false;
        }
        
        for (String role : roles) {
            if (this.roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean isLibrarian() {
        return hasRole("LIBRARIAN") || isAdmin();
    }

    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else {
            return username != null ? username : keycloakId;
        }
    }
}
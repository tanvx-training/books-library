package com.library.apigateway.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * Domain model representing user context extracted from JWT token
 */
@Data
@Builder
public class UserContext {
    private String keycloakId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles;
    private Set<String> permissions;
    private boolean isActive;
    
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
    
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
    
    public boolean hasAnyRole(String... roles) {
        if (this.roles == null) return false;
        for (String role : roles) {
            if (this.roles.contains(role)) {
                return true;
            }
        }
        return false;
    }
}
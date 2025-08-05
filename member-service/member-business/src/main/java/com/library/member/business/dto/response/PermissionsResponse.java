package com.library.member.business.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * Response DTO for user permissions and authorization information.
 * Contains role and permission data for authorization checks.
 */
@Data
@Builder
public class PermissionsResponse {
    
    /**
     * The user's Keycloak identifier.
     */
    private String keycloakId;
    
    /**
     * The user's username.
     */
    private String username;
    
    /**
     * Set of roles assigned to the user.
     */
    private Set<String> roles;
    
    /**
     * Whether the user has admin privileges.
     */
    private boolean isAdmin;
    
    /**
     * Whether the user has librarian privileges.
     */
    private boolean isLibrarian;
    
    /**
     * Whether the user is authenticated.
     */
    private boolean isAuthenticated;
    
    /**
     * Additional permissions or capabilities.
     */
    private Set<String> permissions;
}
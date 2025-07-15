package com.library.apigateway.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * Domain model representing route permission requirements
 */
@Data
@Builder
public class RoutePermission {
    private String path;
    private String method;
    private Set<String> requiredRoles;
    private Set<String> requiredPermissions;
    private boolean requiresAuthentication;
    private boolean isPublic;
    
    public boolean isAccessAllowed(UserContext userContext) {
        if (isPublic) {
            return true;
        }
        
        if (requiresAuthentication && userContext == null) {
            return false;
        }
        
        if (requiredRoles != null && !requiredRoles.isEmpty()) {
            boolean hasRequiredRole = requiredRoles.stream()
                .anyMatch(role -> userContext.hasRole(role));
            if (!hasRequiredRole) {
                return false;
            }
        }
        
        if (requiredPermissions != null && !requiredPermissions.isEmpty()) {
            boolean hasRequiredPermission = requiredPermissions.stream()
                .anyMatch(permission -> userContext.hasPermission(permission));
            if (!hasRequiredPermission) {
                return false;
            }
        }
        
        return true;
    }
}
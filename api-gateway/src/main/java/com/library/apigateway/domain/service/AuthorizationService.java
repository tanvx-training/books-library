package com.library.apigateway.domain.service;

import com.library.apigateway.domain.model.UserContext;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * Domain service for authorization operations
 */
public interface AuthorizationService {
    
    /**
     * Check if user has access to the requested resource
     */
    boolean hasAccess(UserContext userContext, ServerHttpRequest request);
    
    /**
     * Check if the route is public (no authentication required)
     */
    boolean isPublicRoute(String path, String method);
    
    /**
     * Get required roles for a specific route
     */
    java.util.Set<String> getRequiredRoles(String path, String method);
    
    /**
     * Get required permissions for a specific route
     */
    java.util.Set<String> getRequiredPermissions(String path, String method);
}
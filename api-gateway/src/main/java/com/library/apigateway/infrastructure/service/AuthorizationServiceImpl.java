package com.library.apigateway.infrastructure.service;

import com.library.apigateway.domain.model.RoutePermission;
import com.library.apigateway.domain.model.UserContext;
import com.library.apigateway.domain.service.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Infrastructure implementation of authorization service
 */
@Slf4j
@Service
public class AuthorizationServiceImpl implements AuthorizationService {
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final Map<String, RoutePermission> routePermissions;
    
    public AuthorizationServiceImpl() {
        this.routePermissions = initializeRoutePermissions();
    }
    
    @Override
    public boolean hasAccess(UserContext userContext, ServerHttpRequest request) {
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        
        log.debug("Checking access for path: {} method: {}", path, method);
        
        // Check if route is public
        if (isPublicRoute(path, method)) {
            log.debug("Public route access granted for: {}", path);
            return true;
        }
        
        // Check if user is authenticated
        if (userContext == null) {
            log.debug("Access denied - no user context for: {}", path);
            return false;
        }
        
        // Find matching route permission
        RoutePermission routePermission = findMatchingRoutePermission(path, method);
        if (routePermission != null) {
            boolean hasAccess = routePermission.isAccessAllowed(userContext);
            log.debug("Route permission check result: {} for path: {}", hasAccess, path);
            return hasAccess;
        }
        
        // Default: require authentication for non-public routes
        log.debug("Default authentication check for path: {}", path);
        return userContext.isActive();
    }
    
    @Override
    public boolean isPublicRoute(String path, String method) {
        // Public routes that don't require authentication
        String[] publicPaths = {
            "/auth/login",
            "/auth/logout", 
            "/auth/callback/**",
            "/login/oauth2/code/**",
            "/actuator/health",
            "/actuator/info"
        };
        
        for (String publicPath : publicPaths) {
            if (pathMatcher.match(publicPath, path)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public Set<String> getRequiredRoles(String path, String method) {
        RoutePermission routePermission = findMatchingRoutePermission(path, method);
        return routePermission != null ? routePermission.getRequiredRoles() : Set.of();
    }
    
    @Override
    public Set<String> getRequiredPermissions(String path, String method) {
        RoutePermission routePermission = findMatchingRoutePermission(path, method);
        return routePermission != null ? routePermission.getRequiredPermissions() : Set.of();
    }
    
    private RoutePermission findMatchingRoutePermission(String path, String method) {
        for (RoutePermission routePermission : routePermissions.values()) {
            if (pathMatcher.match(routePermission.getPath(), path) && 
                (routePermission.getMethod().equals("*") || routePermission.getMethod().equalsIgnoreCase(method))) {
                return routePermission;
            }
        }
        return null;
    }
    
    private Map<String, RoutePermission> initializeRoutePermissions() {
        Map<String, RoutePermission> permissions = new HashMap<>();
        
        // User service routes
        permissions.put("users-read", RoutePermission.builder()
            .path("/api/users/**")
            .method("GET")
            .requiredRoles(Set.of("USER", "LIBRARIAN", "ADMIN"))
            .requiresAuthentication(true)
            .isPublic(false)
            .build());
            
        permissions.put("users-write", RoutePermission.builder()
            .path("/api/users/**")
            .method("POST")
            .requiredRoles(Set.of("LIBRARIAN", "ADMIN"))
            .requiresAuthentication(true)
            .isPublic(false)
            .build());
            
        permissions.put("users-update", RoutePermission.builder()
            .path("/api/users/**")
            .method("PUT")
            .requiredRoles(Set.of("LIBRARIAN", "ADMIN"))
            .requiresAuthentication(true)
            .isPublic(false)
            .build());
            
        permissions.put("users-delete", RoutePermission.builder()
            .path("/api/users/**")
            .method("DELETE")
            .requiredRoles(Set.of("ADMIN"))
            .requiresAuthentication(true)
            .isPublic(false)
            .build());
        
        // Book service routes
        permissions.put("books-read", RoutePermission.builder()
            .path("/api/books/**")
            .method("GET")
            .requiredRoles(Set.of("USER", "LIBRARIAN", "ADMIN"))
            .requiresAuthentication(true)
            .isPublic(false)
            .build());
            
        permissions.put("books-write", RoutePermission.builder()
            .path("/api/books/**")
            .method("POST")
            .requiredRoles(Set.of("LIBRARIAN", "ADMIN"))
            .requiresAuthentication(true)
            .isPublic(false)
            .build());
        
        // Notification service routes
        permissions.put("notifications-read", RoutePermission.builder()
            .path("/api/notifications/**")
            .method("GET")
            .requiredRoles(Set.of("USER", "LIBRARIAN", "ADMIN"))
            .requiresAuthentication(true)
            .isPublic(false)
            .build());
        
        return permissions;
    }
}
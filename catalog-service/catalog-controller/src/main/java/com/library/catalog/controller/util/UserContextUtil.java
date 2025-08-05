package com.library.catalog.controller.util;

import com.library.catalog.controller.filter.SecurityContextFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Utility class for extracting user context from both API Gateway headers and JWT tokens.
 * This provides a unified interface for getting user information regardless of the authentication source.
 */
public class UserContextUtil {

    private static final String DEFAULT_USER = "system";
    private static final String PREFERRED_USERNAME_CLAIM = "preferred_username";
    private static final String SUB_CLAIM = "sub";
    private static final String EMAIL_CLAIM = "email";

    /**
     * Gets the current user ID, prioritizing API Gateway headers over JWT tokens.
     */
    public static String getCurrentUser() {
        try {
            // First try to get user context from API Gateway headers
            SecurityContextFilter.UserContext userContext = SecurityContextFilter.UserContextHolder.getContext();
            if (userContext != null) {
                return userContext.userId();
            }

            // Fallback to JWT token for direct API calls
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return DEFAULT_USER;
            }

            // Handle JWT authentication token
            if (authentication instanceof JwtAuthenticationToken jwtToken) {
                return extractUserFromJwt(jwtToken.getToken());
            }

            // Handle other authentication types
            if (authentication.getPrincipal() != null) {
                return authentication.getName();
            }

            return DEFAULT_USER;
        } catch (Exception e) {
            // Log the exception but don't fail the operation
            // In a production environment, you might want to use a proper logger
            System.err.println("Error extracting user context: " + e.getMessage());
            return DEFAULT_USER;
        }
    }

    private static String extractUserFromJwt(Jwt jwt) {
        try {
            // Try preferred_username first (common in Keycloak)
            String username = jwt.getClaimAsString(PREFERRED_USERNAME_CLAIM);
            if (username != null && !username.trim().isEmpty()) {
                return username;
            }

            // Try email as fallback
            String email = jwt.getClaimAsString(EMAIL_CLAIM);
            if (email != null && !email.trim().isEmpty()) {
                return email;
            }

            // Use subject as last resort
            String subject = jwt.getClaimAsString(SUB_CLAIM);
            if (subject != null && !subject.trim().isEmpty()) {
                return subject;
            }

            return DEFAULT_USER;
        } catch (Exception e) {
            System.err.println("Error extracting user from JWT: " + e.getMessage());
            return DEFAULT_USER;
        }
    }

    /**
     * Checks if there is an authenticated user (from either API Gateway or JWT).
     */
    public static boolean hasAuthenticatedUser() {
        try {
            // Check API Gateway context first
            SecurityContextFilter.UserContext userContext = SecurityContextFilter.UserContextHolder.getContext();
            if (userContext != null) {
                return true;
            }

            // Check JWT authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null && authentication.isAuthenticated() 
                   && !"anonymousUser".equals(authentication.getPrincipal());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets comprehensive user context including roles and authentication status.
     */
    public static UserContext getCurrentUserContext() {
        // Try API Gateway context first
        SecurityContextFilter.UserContext apiGatewayContext = SecurityContextFilter.UserContextHolder.getContext();
        if (apiGatewayContext != null) {
            return new UserContext(
                apiGatewayContext.userId(),
                true, 
                apiGatewayContext.email(),
                apiGatewayContext.username(),
                apiGatewayContext.roles()
            );
        }

        // Fallback to JWT authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return new UserContext(DEFAULT_USER, false, null, null, null);
        }

        String userId = getCurrentUser();
        boolean isAuthenticated = hasAuthenticatedUser();
        
        return new UserContext(userId, isAuthenticated, null, null, null);
    }

    /**
     * Checks if the current user has the specified role.
     */
    public static boolean hasRole(String role) {
        SecurityContextFilter.UserContext userContext = SecurityContextFilter.UserContextHolder.getContext();
        if (userContext != null) {
            return userContext.hasRole(role);
        }
        
        // For JWT tokens, role checking would need to be implemented
        // This is a simplified version
        return false;
    }

    /**
     * Checks if the current user has any of the specified roles.
     */
    public static boolean hasAnyRole(String... roles) {
        SecurityContextFilter.UserContext userContext = SecurityContextFilter.UserContextHolder.getContext();
        if (userContext != null) {
            return userContext.hasAnyRole(roles);
        }
        
        // For JWT tokens, role checking would need to be implemented
        return false;
    }

    /**
     * Enhanced user context record with role information.
     */
    public record UserContext(
        String userId, 
        boolean authenticated, 
        String email, 
        String username, 
        java.util.Set<String> roles
    ) {
        public boolean hasRole(String role) {
            return roles != null && roles.contains(role);
        }
        
        public boolean hasAnyRole(String... roles) {
            if (this.roles == null) {
                return false;
            }
            
            for (String role : roles) {
                if (this.roles.contains(role)) {
                    return true;
                }
            }
            return false;
        }
    }
}
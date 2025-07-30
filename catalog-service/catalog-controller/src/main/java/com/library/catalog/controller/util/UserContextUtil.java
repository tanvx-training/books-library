package com.library.catalog.controller.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Utility class for extracting user context information from Spring Security context.
 * Handles JWT tokens and provides fallback mechanisms when user context is not available.
 */
public class UserContextUtil {

    private static final String DEFAULT_USER = "system";
    private static final String PREFERRED_USERNAME_CLAIM = "preferred_username";
    private static final String SUB_CLAIM = "sub";
    private static final String EMAIL_CLAIM = "email";

    /**
     * Extracts the current user identifier from the security context.
     * 
     * @return the current user identifier, or "system" if no user context is available
     */
    public static String getCurrentUser() {
        try {
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

    /**
     * Extracts user identifier from JWT token.
     * Tries multiple claims in order of preference: preferred_username, email, sub.
     * 
     * @param jwt the JWT token
     * @return the user identifier from the token
     */
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
     * Checks if there is an authenticated user in the current security context.
     * 
     * @return true if there is an authenticated user, false otherwise
     */
    public static boolean hasAuthenticatedUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null && authentication.isAuthenticated() 
                   && !"anonymousUser".equals(authentication.getPrincipal());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the current user with additional context information.
     * This method can be extended to include more user details if needed.
     * 
     * @return UserContext object with user information
     */
    public static UserContext getCurrentUserContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return new UserContext(DEFAULT_USER, false);
        }

        String userId = getCurrentUser();
        boolean isAuthenticated = hasAuthenticatedUser();
        
        return new UserContext(userId, isAuthenticated);
    }

    /**
         * Simple data class to hold user context information.
         */
        public record UserContext(String userId, boolean authenticated) {

    }
}
package com.library.catalog.controller.filter;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter to extract user context from API Gateway headers and set it in thread-local storage.
 * This filter processes requests that come through the API Gateway with user context headers.
 * For direct API calls with JWT tokens, the JwtAuthenticationService handles user context extraction.
 */
@Component
@Order(1) // Execute early in the filter chain
public class SecurityContextFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityContextFilter.class);

    // Header names expected from API Gateway
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLES_HEADER = "X-User-Roles";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USERNAME_HEADER = "X-Username";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // Extract user context from API Gateway headers
            UserContext userContext = extractUserContext(httpRequest);
            
            if (userContext != null) {
                // Set user context in thread-local storage for API Gateway requests
                UserContextHolder.setContext(userContext);
                logger.debug("User context set from API Gateway headers for user: {}", userContext.userId());
            } else {
                logger.debug("No API Gateway user context headers found for path: {}, will rely on JWT authentication", 
                           httpRequest.getRequestURI());
                // Note: For direct API calls with JWT tokens, the existing UserContextUtil will handle user context
                // Spring Security will handle authentication/authorization for JWT tokens
            }

            // Continue with the filter chain
            chain.doFilter(request, response);

        } catch (Exception e) {
            logger.error("Error processing user context from headers", e);
            // Don't fail the request, let Spring Security handle authentication
            chain.doFilter(request, response);
        } finally {
            // Always clear the context to prevent memory leaks
            UserContextHolder.clearContext();
        }
    }

    /**
     * Extracts user context from API Gateway headers.
     * Returns null if headers are not present (indicating direct API call with JWT).
     */
    private UserContext extractUserContext(HttpServletRequest request) {
        String userId = request.getHeader(USER_ID_HEADER);
        String email = request.getHeader(USER_EMAIL_HEADER);
        String username = request.getHeader(USERNAME_HEADER);
        String rolesHeader = request.getHeader(USER_ROLES_HEADER);

        // If no user ID header, this is likely a direct API call with JWT token
        if (userId == null || userId.trim().isEmpty()) {
            logger.debug("No API Gateway user ID header found, assuming direct JWT API call");
            return null;
        }

        // Parse roles from comma-separated string
        Set<String> roles = new HashSet<>();
        if (rolesHeader != null && !rolesHeader.trim().isEmpty()) {
            String[] roleArray = rolesHeader.split(",");
            for (String role : roleArray) {
                String trimmedRole = role.trim();
                if (!trimmedRole.isEmpty()) {
                    roles.add(trimmedRole);
                }
            }
        }

        logger.debug("Extracted user context from API Gateway headers - User: {}, Roles: {}", userId, roles);
        return new UserContext(userId.trim(), email, username, roles);
    }

    /**
     * Simple user context holder for API Gateway headers.
     */
    public static class UserContextHolder {
        private static final ThreadLocal<UserContext> contextHolder = new ThreadLocal<>();

        public static void setContext(UserContext userContext) {
            contextHolder.set(userContext);
        }

        public static UserContext getContext() {
            return contextHolder.get();
        }

        public static void clearContext() {
            contextHolder.remove();
        }

        public static boolean hasContext() {
            return contextHolder.get() != null;
        }

        public static String getCurrentUserKeycloakId() {
            UserContext context = getContext();
            return context != null ? context.userId() : "SYSTEM";
        }
    }

        public record UserContext(String userId, String email, String username, Set<String> roles) {

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
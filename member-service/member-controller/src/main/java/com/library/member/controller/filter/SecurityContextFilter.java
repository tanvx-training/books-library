package com.library.member.controller.filter;

import com.library.member.business.security.UserContext;
import com.library.member.business.security.UserContextHolder;
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

@Component
@Order(1) // Execute early in the filter chain
public class SecurityContextFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityContextFilter.class);

    // Header names expected from API Gateway
    private static final String USER_ID_HEADER = "X-User-ID";
    private static final String USER_ROLES_HEADER = "X-User-Roles";
    private static final String USER_EMAIL_HEADER = "X-User-Email";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // Extract user context from headers
            UserContext userContext = extractUserContext(httpRequest);
            
            if (userContext != null) {
                // Set user context in thread-local storage
                UserContextHolder.setContext(userContext);
                logger.debug("User context set for user: {}", userContext.getUserId());
            } else {
                logger.warn("No valid user context found in request headers for path: {}", 
                           httpRequest.getRequestURI());
                
                // For endpoints that require authentication, return 403
                if (requiresAuthentication(httpRequest)) {
                    httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    httpResponse.setContentType("application/json");
                    httpResponse.getWriter().write(
                        "{\"message\":\"Access denied: Missing or invalid user context\",\"errorCode\":\"MISSING_USER_CONTEXT\"}"
                    );
                    return;
                }
            }

            // Continue with the filter chain
            chain.doFilter(request, response);

        } catch (Exception e) {
            logger.error("Error processing security context", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                "{\"message\":\"Internal server error\",\"errorCode\":\"SECURITY_CONTEXT_ERROR\"}"
            );
        } finally {
            // Always clear the context to prevent memory leaks
            UserContextHolder.clearContext();
        }
    }

    private UserContext extractUserContext(HttpServletRequest request) {
        String userId = request.getHeader(USER_ID_HEADER);
        String email = request.getHeader(USER_EMAIL_HEADER);
        String rolesHeader = request.getHeader(USER_ROLES_HEADER);

        // User ID is required
        if (userId == null || userId.trim().isEmpty()) {
            logger.debug("Missing or empty user ID header");
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

        return new UserContext(userId.trim(), email, roles);
    }
    private boolean requiresAuthentication(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip authentication for health checks and actuator endpoints
        if (path.startsWith("/actuator/") || 
            path.equals("/health") || 
            path.equals("/info") ||
            path.startsWith("/swagger-") ||
            path.startsWith("/v3/api-docs")) {
            return false;
        }

        // All API endpoints require authentication
        return path.startsWith("/api/");
    }
}
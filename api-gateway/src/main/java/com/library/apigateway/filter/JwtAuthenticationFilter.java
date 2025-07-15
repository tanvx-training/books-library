package com.library.apigateway.filter;

import com.library.apigateway.domain.model.UserContext;
import com.library.apigateway.domain.service.AuthorizationService;
import com.library.apigateway.domain.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * JWT Authentication Filter for API Gateway
 * Extracts user context from JWT and adds headers for downstream services
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
    
    private final JwtTokenService jwtTokenService;
    private final AuthorizationService authorizationService;
    private final ReactiveJwtDecoder jwtDecoder;
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    // Headers to pass to downstream services
    private static final String X_USER_ID = "X-User-Id";
    private static final String X_USERNAME = "X-Username";
    private static final String X_USER_EMAIL = "X-User-Email";
    private static final String X_USER_ROLES = "X-User-Roles";
    private static final String X_USER_PERMISSIONS = "X-User-Permissions";
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        
        log.debug("Processing request: {} {}", method, path);
        
        // Check if route is public
        if (authorizationService.isPublicRoute(path, method)) {
            log.debug("Public route, skipping authentication: {}", path);
            return chain.filter(exchange);
        }
        
        // Extract JWT token
        String token = extractToken(request);
        if (token == null) {
            log.debug("No token found for protected route: {}", path);
            return handleUnauthorized(exchange);
        }
        
        // Decode and validate JWT
        return jwtDecoder.decode(token)
            .cast(Jwt.class)
            .flatMap(jwt -> processJwtToken(jwt, exchange, chain))
            .onErrorResume(error -> {
                log.error("JWT validation failed for path: {}", path, error);
                return handleUnauthorized(exchange);
            });
    }
    
    private Mono<Void> processJwtToken(Jwt jwt, ServerWebExchange exchange, WebFilterChain chain) {
        try {
            // Extract user context from JWT
            UserContext userContext = jwtTokenService.extractUserContext(jwt);
            if (userContext == null) {
                log.warn("Failed to extract user context from JWT");
                return handleUnauthorized(exchange);
            }
            
            // Check authorization
            if (!authorizationService.hasAccess(userContext, exchange.getRequest())) {
                log.warn("Access denied for user: {} to path: {}", 
                    userContext.getUsername(), exchange.getRequest().getURI().getPath());
                return handleForbidden(exchange);
            }
            
            // Add user context headers for downstream services
            ServerHttpRequest modifiedRequest = addUserContextHeaders(exchange.getRequest(), userContext);
            ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
            
            log.debug("Authentication successful for user: {} path: {}", 
                userContext.getUsername(), exchange.getRequest().getURI().getPath());
            
            return chain.filter(modifiedExchange);
            
        } catch (Exception e) {
            log.error("Error processing JWT token", e);
            return handleUnauthorized(exchange);
        }
    }
    
    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }
    
    private ServerHttpRequest addUserContextHeaders(ServerHttpRequest request, UserContext userContext) {
        return request.mutate()
            .header(X_USER_ID, userContext.getKeycloakId())
            .header(X_USERNAME, userContext.getUsername())
            .header(X_USER_EMAIL, userContext.getEmail() != null ? userContext.getEmail() : "")
            .header(X_USER_ROLES, String.join(",", userContext.getRoles()))
            .header(X_USER_PERMISSIONS, String.join(",", userContext.getPermissions()))
            .build();
    }
    
    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
    
    private Mono<Void> handleForbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }
}
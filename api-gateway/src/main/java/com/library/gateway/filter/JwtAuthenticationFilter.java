package com.library.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final ReactiveJwtDecoder jwtDecoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // JWT claim names
    private static final String USER_ID_CLAIM = "sub";
    private static final String USERNAME_CLAIM = "preferred_username";
    private static final String EMAIL_CLAIM = "email";
    private static final String FIRST_NAME_CLAIM = "given_name";
    private static final String LAST_NAME_CLAIM = "family_name";
    private static final String ROLES_CLAIM = "realm_access";
    private static final String ROLES_SUB_CLAIM = "roles";

    // Header names to forward to downstream services
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USERNAME_HEADER = "X-Username";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLES_HEADER = "X-User-Roles";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Skip authentication for health checks and public endpoints
        if (isPublicEndpoint(path)) {
            log.debug("Skipping JWT authentication for public endpoint: {}", path);
            return chain.filter(exchange);
        }

        // Extract JWT token from Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return handleUnauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix

        // Decode and validate JWT token reactively
        return jwtDecoder.decode(token)
                .doOnNext(jwt -> log.debug("JWT token validated successfully for user: {}", jwt.getSubject()))
                .map(this::extractUserContext)
                .map(userContext -> exchange
                        .mutate()
                        .request(addUserContextHeaders(request, userContext))
                        .build()
                )
                .flatMap(chain::filter)
                .onErrorResume(JwtException.class, e -> {
                    log.error("JWT validation failed for path: {}", path, e);
                    return handleUnauthorized(exchange, "Invalid JWT token");
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("Error processing JWT token for path: {}", path, e);
                    return handleInternalError(exchange);
                });
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/actuator/") ||
               path.equals("/health") ||
               path.equals("/info") ||
               path.startsWith("/swagger-") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/webjars/") ||
               path.equals("/favicon.ico");
    }

    private UserContext extractUserContext(Jwt jwt) {
        String userId = jwt.getClaimAsString(USER_ID_CLAIM);
        String username = jwt.getClaimAsString(USERNAME_CLAIM);
        String email = jwt.getClaimAsString(EMAIL_CLAIM);
        String firstName = jwt.getClaimAsString(FIRST_NAME_CLAIM);
        String lastName = jwt.getClaimAsString(LAST_NAME_CLAIM);

        // Extract roles from nested claim structure
        Set<String> roles = extractRoles(jwt);

        return new UserContext(userId, username, email, firstName, lastName, roles);
    }

    private Set<String> extractRoles(Jwt jwt) {
        Set<String> roles = new HashSet<>();

        try {
            Map<String, Object> realmAccess = jwt.getClaimAsMap(ROLES_CLAIM);
            if (realmAccess != null && realmAccess.containsKey(ROLES_SUB_CLAIM)) {
                Object rolesObj = realmAccess.get(ROLES_SUB_CLAIM);
                if (rolesObj instanceof List<?> rolesList) {
                    for (Object role : rolesList) {
                        if (role instanceof String roleStr) {
                            roles.add(roleStr);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error extracting roles from JWT token, using empty roles set", e);
        }

        return roles;
    }

    private ServerHttpRequest addUserContextHeaders(ServerHttpRequest request, UserContext userContext) {
        ServerHttpRequest.Builder builder = request.mutate();

        // Add user ID header (required)
        if (userContext.userId() != null) {
            builder.header(USER_ID_HEADER, userContext.userId());
        }

        // Add username header
        if (userContext.username() != null) {
            builder.header(USERNAME_HEADER, userContext.username());
        }

        // Add email header
        if (userContext.email() != null) {
            builder.header(USER_EMAIL_HEADER, userContext.email());
        }

        // Add roles header (comma-separated)
        if (userContext.roles() != null && !userContext.roles().isEmpty()) {
            String rolesString = String.join(",", userContext.roles());
            builder.header(USER_ROLES_HEADER, rolesString);
        }

        log.debug("Added user context headers for user: {} with roles: {}",
                    userContext.userId(), userContext.roles());

        return builder.build();
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        String body = createErrorResponse("UNAUTHORIZED", message);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    private Mono<Void> handleInternalError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        String body = createErrorResponse("INTERNAL_ERROR", "Authentication processing error");
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    private String createErrorResponse(String errorCode, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("errorCode", errorCode);
        error.put("message", message);
        error.put("timestamp", new Date().toString());

        try {
            return objectMapper.writeValueAsString(error);
        } catch (JsonProcessingException e) {
            log.error("Error creating JSON error response", e);
            return "{\"errorCode\":\"" + errorCode + "\",\"message\":\"" + message + "\"}";
        }
    }

    @Override
    public int getOrder() {
        return -1; // Execute after RequestLoggingFilter but before other filters
    }

        private record UserContext(String userId, String username, String email, String firstName, String lastName,
                                   Set<String> roles) {

    }
}
package com.library.user.domain.constant;

/**
 * Constants for security configuration across all services
 */
public class SecurityConstants {
    
    // JWT related constants
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String JWT_ISSUER = "library-user-service";
    public static final String JWT_AUDIENCE = "library-services";
    public static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000; // 15 minutes
    public static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7 days
    
    // Headers for user context propagation
    public static final String HEADER_USER_ID = "X-User-ID";
    public static final String HEADER_USER_ROLES = "X-User-Roles";
    
    // API endpoints
    public static final String AUTH_LOGIN_URL = "/api/auth/login";
    public static final String AUTH_REGISTER_URL = "/api/auth/register";
    public static final String AUTH_REFRESH_URL = "/api/auth/refresh";
    public static final String AUTH_LOGOUT_URL = "/api/auth/logout";
    public static final String AUTH_JWKS_URL = "/api/auth/.well-known/jwks.json";
    
    // Redis key prefixes
    public static final String REDIS_REFRESH_TOKEN_PREFIX = "refresh_token:";
    public static final String REDIS_BLACKLIST_PREFIX = "blacklist:access:";
    
    private SecurityConstants() {
        // Private constructor to prevent instantiation
    }
} 
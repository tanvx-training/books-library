package com.library.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * Utility class for HTTP operations.
 * Provides methods for common HTTP operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpUtils {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String USER_AGENT_HEADER = "User-Agent";

    /**
     * Gets the current HTTP request.
     *
     * @return an Optional containing the current request, or empty if not available
     */
    public static Optional<HttpServletRequest> getCurrentRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }

    /**
     * Gets the client IP address from the current request.
     *
     * @return the client IP address, or null if not available
     */
    public static String getClientIpAddress() {
        return getCurrentRequest().map(HttpUtils::getClientIpAddress).orElse(null);
    }

    /**
     * Gets the client IP address from the specified request.
     *
     * @param request the HTTP request
     * @return the client IP address
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For can contain multiple IP addresses in the format:
            // "client, proxy1, proxy2"
            // We want the client's IP address, which is the first one
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Gets the authorization header from the current request.
     *
     * @return the authorization header, or null if not available
     */
    public static String getAuthorizationHeader() {
        return getCurrentRequest()
                .map(request -> request.getHeader(AUTHORIZATION_HEADER))
                .orElse(null);
    }

    /**
     * Gets the bearer token from the current request.
     *
     * @return the bearer token, or null if not available
     */
    public static String getBearerToken() {
        String authHeader = getAuthorizationHeader();
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Gets the correlation ID from the current request.
     *
     * @return the correlation ID, or null if not available
     */
    public static String getCorrelationId() {
        return getCurrentRequest()
                .map(request -> request.getHeader(CORRELATION_ID_HEADER))
                .orElse(null);
    }

    /**
     * Gets the user agent from the current request.
     *
     * @return the user agent, or null if not available
     */
    public static String getUserAgent() {
        return getCurrentRequest()
                .map(request -> request.getHeader(USER_AGENT_HEADER))
                .orElse(null);
    }

    /**
     * Creates HTTP headers with the authorization header.
     *
     * @param token the bearer token
     * @return the HTTP headers
     */
    public static HttpHeaders createHeadersWithAuth(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
        return headers;
    }

    /**
     * Creates HTTP headers with the correlation ID.
     *
     * @param correlationId the correlation ID
     * @return the HTTP headers
     */
    public static HttpHeaders createHeadersWithCorrelationId(String correlationId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CORRELATION_ID_HEADER, correlationId);
        return headers;
    }
} 
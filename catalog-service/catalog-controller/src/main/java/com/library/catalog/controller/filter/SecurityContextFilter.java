package com.library.catalog.controller.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Simplified security context filter that only handles correlation ID setup.
 * Authentication is now fully handled by Spring Security OAuth2 Resource Server.
 */
@Slf4j
@Component
@Order(1)
public class SecurityContextFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestPath = httpRequest.getRequestURI();

        try {
            // Set up correlation ID for request tracking
            setupCorrelationId(httpRequest);
            
            log.debug("Processing request: {}", requestPath);
            
            // Continue with the filter chain - Spring Security will handle authentication
            chain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Unexpected error in security context filter for path: {}", requestPath, e);
            // Don't fail the request, continue with the chain
            chain.doFilter(request, response);
        }
    }

    private void setupCorrelationId(HttpServletRequest request) {
        // Try to get correlation ID from headers
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = request.getHeader(REQUEST_ID_HEADER);
        }

        // Generate new correlation ID if not provided
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        // Store correlation ID as request attribute for logging
        request.setAttribute("correlationId", correlationId);
        
        log.debug("Correlation ID set: {}", correlationId);
    }
}
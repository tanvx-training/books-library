package com.library.common.aop.filter;

import com.library.common.constants.LoggingConstants;
import com.library.common.utils.logging.LoggingContextManager;
import com.library.common.utils.logging.MDCContextManager;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Servlet filter to initialize logging context for HTTP requests
 */
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("LoggingFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        long startTime = System.currentTimeMillis();

        try {
            // Initialize logging context from request
            LoggingContextManager.initializeFromRequest(httpRequest);
            
            // Set service name based on request context or application property
            String serviceName = extractServiceName(httpRequest);
            MDCContextManager.setServiceName(serviceName);

            // Log incoming request (basic level)
            if (LoggingContextManager.shouldLog(com.library.common.enums.LogLevel.BASIC)) {
                logger.info("{} - {} {} from {} (User-Agent: {})", 
                    LoggingConstants.REQUEST_RECEIVED,
                    httpRequest.getMethod(), 
                    httpRequest.getRequestURL(),
                    MDCContextManager.getCurrentContext().get(LoggingConstants.CLIENT_IP),
                    httpRequest.getHeader("User-Agent"));
            }

            // Continue with the request
            chain.doFilter(request, response);

            // Log request completion
            long executionTime = System.currentTimeMillis() - startTime;
            MDCContextManager.setExecutionTime(executionTime);

            logRequestCompletion(httpRequest, httpResponse, executionTime, true);

        } catch (Exception e) {
            // Log request failure
            long executionTime = System.currentTimeMillis() - startTime;
            MDCContextManager.setExecutionTime(executionTime);
            MDCContextManager.setBusinessLogicStatus("FAILURE");

            logRequestCompletion(httpRequest, httpResponse, executionTime, false);
            
            logger.error("{} - {} {} failed after {}ms: {}", 
                LoggingConstants.REQUEST_FAILED,
                httpRequest.getMethod(), 
                httpRequest.getRequestURL(),
                executionTime,
                e.getMessage(), e);

            if (e instanceof ServletException) {
                throw e;
            } else if (e instanceof IOException) {
                throw e;
            } else {
                throw new ServletException("Request processing failed", e);
            }

        } finally {
            // Clear logging context to prevent memory leaks
            LoggingContextManager.clearContext();
        }
    }

    @Override
    public void destroy() {
        logger.info("LoggingFilter destroyed");
    }

    /**
     * Log request completion with performance metrics
     */
    private void logRequestCompletion(HttpServletRequest request, HttpServletResponse response, 
                                    long executionTime, boolean successful) {
        try {
            int statusCode = response.getStatus();
            MDCContextManager.addCustomProperty("responseStatus", String.valueOf(statusCode));

            if (LoggingContextManager.shouldLog(com.library.common.enums.LogLevel.BASIC)) {
                if (executionTime > LoggingConstants.VERY_SLOW_REQUEST_THRESHOLD) {
                    logger.warn("{} - {} {} completed with status {} in {}ms (VERY SLOW)", 
                        LoggingConstants.REQUEST_COMPLETED,
                        request.getMethod(), 
                        request.getRequestURL(),
                        statusCode,
                        executionTime);
                } else if (executionTime > LoggingConstants.SLOW_REQUEST_THRESHOLD) {
                    logger.warn("{} - {} {} completed with status {} in {}ms (SLOW)", 
                        LoggingConstants.REQUEST_COMPLETED,
                        request.getMethod(), 
                        request.getRequestURL(),
                        statusCode,
                        executionTime);
                } else {
                    logger.info("{} - {} {} completed with status {} in {}ms", 
                        LoggingConstants.REQUEST_COMPLETED,
                        request.getMethod(), 
                        request.getRequestURL(),
                        statusCode,
                        executionTime);
                }
            }

            // Log performance metrics if enabled
            if (LoggingContextManager.shouldLogPerformanceMetrics()) {
                logger.info("HTTP_PERFORMANCE_METRIC: method={}, url={}, status={}, " +
                    "executionTime={}ms, successful={}, requestId={}, correlationId={}", 
                    request.getMethod(),
                    request.getRequestURL(),
                    statusCode,
                    executionTime,
                    successful,
                    MDCContextManager.getCurrentRequestId(),
                    MDCContextManager.getCurrentCorrelationId());
            }

        } catch (Exception e) {
            logger.debug("Error logging request completion", e);
        }
    }

    /**
     * Extract service name from request context
     */
    private String extractServiceName(HttpServletRequest request) {
        // Try to get service name from various sources
        String serviceName = null;
        
        // First try request header
        serviceName = request.getHeader("X-Service-Name");
        if (serviceName != null && !serviceName.isEmpty()) {
            return serviceName;
        }

        // Try to extract from request path
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty()) {
            return contextPath.replaceFirst("^/", "");
        }

        // Try to extract from server name or port
        String serverName = request.getServerName();
        if (serverName != null && !serverName.equals("localhost") && !serverName.equals("127.0.0.1")) {
            return serverName;
        }

        // Default fallback
        return "unknown-service";
    }
} 
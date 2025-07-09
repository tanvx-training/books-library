package com.library.book.infrastructure.logging;

import com.library.book.infrastructure.enums.LogLevel;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * AOP Aspect specifically for HTTP request/response logging with detailed information
 */
@Aspect
@Component
@Order(2)
public class HttpLoggingAspect {

    // Performance Thresholds (in milliseconds)
    public static final long SLOW_REQUEST_THRESHOLD = 1000L;
    public static final long VERY_SLOW_REQUEST_THRESHOLD = 5000L;

    private static final Logger logger = LoggerFactory.getLogger(HttpLoggingAspect.class);

    /**
     * Pointcut for all RestController methods
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restControllerMethods() {}

    /**
     * Pointcut for all RequestMapping annotated methods
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void requestMappingMethods() {}

    /**
     * Around advice for HTTP endpoints with detailed logging
     */
    @Around("restControllerMethods() && requestMappingMethods()")
    public Object logHttpRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        
        // Only log detailed HTTP info if detailed logging is enabled
        if (!LoggingContextManager.shouldLog(LogLevel.DETAILED)) {
            return joinPoint.proceed();
        }

        long startTime = System.currentTimeMillis();
        Object result;
        Throwable exception = null;

        try {
            // Log request details
            logRequestDetails(joinPoint);

            // Execute the endpoint method
            result = joinPoint.proceed();

            // Log response details
            logResponseDetails(result);

            return result;

        } catch (Throwable throwable) {
            exception = throwable;
            logErrorResponse(throwable);
            throw throwable;

        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            logHttpPerformance(joinPoint, executionTime, exception == null);
        }
    }

    /**
     * Log detailed request information
     */
    private void logRequestDetails(ProceedingJoinPoint joinPoint) {
        try {
            HttpServletRequest request = getCurrentHttpRequest();
            if (request == null) {
                return;
            }

            // Log request headers if advanced logging is enabled
            if (LoggingContextManager.shouldEnableAdvancedTracing()) {
                Map<String, String> headers = getRequestHeaders(request);
                Map<String, String> sanitizedHeaders = sanitizeHeaders(headers);
                
                logger.debug("HTTP_REQUEST_HEADERS: {}", sanitizedHeaders);
            }

            // Log request parameters if detailed logging is enabled
            if (LoggingContextManager.shouldLogMethodArguments()) {
                Map<String, String[]> parameters = request.getParameterMap();
                if (!parameters.isEmpty()) {
                    Map<String, Object> sanitizedParams = sanitizeParameters(parameters);
                    logger.debug("HTTP_REQUEST_PARAMS: {}", sanitizedParams);
                }
            }

            // Log method arguments
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0 && LoggingContextManager.shouldLogMethodArguments()) {
                Object[] sanitizedArgs = DataSanitizer.sanitizeMethodArguments(args);
                logger.debug("HTTP_REQUEST_BODY: method={}.{}, args={}", 
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(sanitizedArgs));
            }

        } catch (Exception e) {
            logger.debug("Error logging HTTP request details", e);
        }
    }

    /**
     * Log detailed response information
     */
    private void logResponseDetails(Object result) {
        try {
            if (!LoggingContextManager.shouldLogReturnValues()) {
                return;
            }

            if (result instanceof ResponseEntity<?> responseEntity) {

                // Log response status
                logger.debug("HTTP_RESPONSE_STATUS: {}", responseEntity.getStatusCode());
                
                // Log response headers if advanced logging is enabled
                if (LoggingContextManager.shouldEnableAdvancedTracing() && responseEntity.hasBody()) {
                    logger.debug("HTTP_RESPONSE_HEADERS: {}", responseEntity.getHeaders());
                }
                
                // Log response body
                Object body = responseEntity.getBody();
                if (body != null) {
                    Object sanitizedBody = DataSanitizer.sanitizeForLogging(body);
                    logger.debug("HTTP_RESPONSE_BODY: {}", sanitizedBody);
                }
                
            } else if (result != null) {
                // Log direct return value
                Object sanitizedResult = DataSanitizer.sanitizeForLogging(result);
                logger.debug("HTTP_RESPONSE_DIRECT: {}", sanitizedResult);
            }

        } catch (Exception e) {
            logger.debug("Error logging HTTP response details", e);
        }
    }

    /**
     * Log error response details
     */
    private void logErrorResponse(Throwable throwable) {
        try {
            String sanitizedError = DataSanitizer.sanitizeException(throwable);
            logger.error("HTTP_ERROR_RESPONSE: {}", sanitizedError);
            
            // Add error context to MDC
            MDCContextManager.setBusinessLogicStatus("HTTP_ERROR");
            String errorCode = extractHttpErrorCode(throwable);
            MDCContextManager.setErrorCode(errorCode);

        } catch (Exception e) {
            logger.debug("Error logging HTTP error response", e);
        }
    }

    /**
     * Log HTTP performance metrics
     */
    private void logHttpPerformance(ProceedingJoinPoint joinPoint, long executionTime, boolean successful) {
        try {
            HttpServletRequest request = getCurrentHttpRequest();
            if (request == null) {
                return;
            }

            String endpoint = String.format("%s %s", request.getMethod(), request.getRequestURI());
            String controllerMethod = String.format("%s.%s", 
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName());

            if (executionTime > VERY_SLOW_REQUEST_THRESHOLD) {
                logger.warn("HTTP_VERY_SLOW_ENDPOINT: endpoint={}, method={}, executionTime={}ms, " +
                    "successful={}, requestId={}", 
                    endpoint, controllerMethod, executionTime, successful,
                    MDCContextManager.getCurrentRequestId());
            } else if (executionTime > SLOW_REQUEST_THRESHOLD) {
                logger.warn("HTTP_SLOW_ENDPOINT: endpoint={}, method={}, executionTime={}ms, " +
                    "successful={}, requestId={}", 
                    endpoint, controllerMethod, executionTime, successful,
                    MDCContextManager.getCurrentRequestId());
            } else {
                logger.debug("HTTP_ENDPOINT_PERFORMANCE: endpoint={}, method={}, executionTime={}ms, " +
                    "successful={}, requestId={}", 
                    endpoint, controllerMethod, executionTime, successful,
                    MDCContextManager.getCurrentRequestId());
            }

        } catch (Exception e) {
            logger.debug("Error logging HTTP performance metrics", e);
        }
    }

    /**
     * Get current HTTP request from RequestContextHolder
     */
    private HttpServletRequest getCurrentHttpRequest() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract HTTP request headers
     */
    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
        
        return headers;
    }

    /**
     * Sanitize HTTP headers to remove sensitive information
     */
    private Map<String, String> sanitizeHeaders(Map<String, String> headers) {
        Map<String, String> sanitizedHeaders = new HashMap<>();
        
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String headerName = entry.getKey().toLowerCase();
            String headerValue = entry.getValue();
            
            // Sanitize sensitive headers
            if (headerName.contains("authorization") || 
                headerName.contains("token") || 
                headerName.contains("key") ||
                headerName.contains("secret") ||
                headerName.contains("password")) {
                sanitizedHeaders.put(entry.getKey(), "***MASKED***");
            } else {
                sanitizedHeaders.put(entry.getKey(), headerValue);
            }
        }
        
        return sanitizedHeaders;
    }

    /**
     * Sanitize request parameters
     */
    private Map<String, Object> sanitizeParameters(Map<String, String[]> parameters) {
        Map<String, Object> sanitizedParams = new HashMap<>();
        
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            String paramName = entry.getKey().toLowerCase();
            String[] paramValues = entry.getValue();
            
            // Sanitize sensitive parameters
            if (paramName.contains("password") || 
                paramName.contains("token") || 
                paramName.contains("key") ||
                paramName.contains("secret")) {
                sanitizedParams.put(entry.getKey(), "***MASKED***");
            } else {
                // Show array if multiple values, single value otherwise
                if (paramValues.length == 1) {
                    sanitizedParams.put(entry.getKey(), paramValues[0]);
                } else {
                    sanitizedParams.put(entry.getKey(), Arrays.toString(paramValues));
                }
            }
        }
        
        return sanitizedParams;
    }

    /**
     * Extract HTTP-specific error code from exception
     */
    private String extractHttpErrorCode(Throwable throwable) {
        String className = throwable.getClass().getSimpleName();
        
        // Map common HTTP exceptions to codes
        if (className.contains("BadRequest")) return "HTTP_400";
        if (className.contains("Unauthorized")) return "HTTP_401";
        if (className.contains("Forbidden")) return "HTTP_403";
        if (className.contains("NotFound")) return "HTTP_404";
        if (className.contains("MethodNotAllowed")) return "HTTP_405";
        if (className.contains("Conflict")) return "HTTP_409";
        if (className.contains("Validation")) return "HTTP_422";
        if (className.contains("InternalServerError")) return "HTTP_500";
        if (className.contains("ServiceUnavailable")) return "HTTP_503";
        
        return "HTTP_ERROR";
    }
} 
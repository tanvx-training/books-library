package com.library.common.utils.logging;

import com.library.common.constants.LoggingConstants;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.UUID;

/**
 * Utility class for managing MDC context for logging
 */
public final class MDCContextManager {
    
    private MDCContextManager() {
        // Utility class
    }
    
    /**
     * Initialize MDC context with basic request information
     */
    public static void initializeContext() {
        String requestId = generateRequestId();
        MDC.put(LoggingConstants.REQUEST_ID, requestId);
        MDC.put(LoggingConstants.CORRELATION_ID, requestId); // Default correlation ID to request ID
    }
    
    /**
     * Initialize MDC context with provided correlation ID
     */
    public static void initializeContext(String correlationId) {
        String requestId = generateRequestId();
        MDC.put(LoggingConstants.REQUEST_ID, requestId);
        MDC.put(LoggingConstants.CORRELATION_ID, 
                StringUtils.hasText(correlationId) ? correlationId : requestId);
    }
    
    /**
     * Set HTTP request context information
     */
    public static void setHttpContext(String method, String url, String clientIp, String userAgent) {
        MDC.put(LoggingConstants.HTTP_METHOD, method);
        MDC.put(LoggingConstants.REQUEST_URL, url);
        MDC.put(LoggingConstants.CLIENT_IP, clientIp);
        MDC.put(LoggingConstants.USER_AGENT, userAgent);
    }
    
    /**
     * Set user context information
     */
    public static void setUserContext(String userId) {
        if (StringUtils.hasText(userId)) {
            MDC.put(LoggingConstants.USER_ID, userId);
        }
    }
    
    /**
     * Set method execution context
     */
    public static void setMethodContext(String className, String methodName) {
        MDC.put(LoggingConstants.CLASS_NAME, className);
        MDC.put(LoggingConstants.METHOD_NAME, methodName);
    }
    
    /**
     * Set execution time
     */
    public static void setExecutionTime(long executionTimeMs) {
        MDC.put(LoggingConstants.EXECUTION_TIME, String.valueOf(executionTimeMs));
    }
    
    /**
     * Set business logic status
     */
    public static void setBusinessLogicStatus(String status) {
        MDC.put(LoggingConstants.BUSINESS_LOGIC_STATUS, status);
    }
    
    /**
     * Set data source information
     */
    public static void setDataSource(String dataSource) {
        MDC.put(LoggingConstants.DATA_SOURCE, dataSource);
    }
    
    /**
     * Set service name
     */
    public static void setServiceName(String serviceName) {
        MDC.put(LoggingConstants.SERVICE_NAME, serviceName);
    }
    
    /**
     * Set operation type
     */
    public static void setOperationType(String operationType) {
        MDC.put(LoggingConstants.OPERATION_TYPE, operationType);
    }
    
    /**
     * Set resource type
     */
    public static void setResourceType(String resourceType) {
        MDC.put(LoggingConstants.RESOURCE_TYPE, resourceType);
    }
    
    /**
     * Set error code for error scenarios
     */
    public static void setErrorCode(String errorCode) {
        MDC.put(LoggingConstants.ERROR_CODE, errorCode);
    }
    
    /**
     * Set span ID for distributed tracing
     */
    public static void setSpanId(String spanId) {
        if (StringUtils.hasText(spanId)) {
            MDC.put(LoggingConstants.SPAN_ID, spanId);
        }
    }
    
    /**
     * Set parent span ID for distributed tracing
     */
    public static void setParentSpanId(String parentSpanId) {
        if (StringUtils.hasText(parentSpanId)) {
            MDC.put(LoggingConstants.PARENT_SPAN_ID, parentSpanId);
        }
    }
    
    /**
     * Add custom property to MDC
     */
    public static void addCustomProperty(String key, String value) {
        if (StringUtils.hasText(key) && StringUtils.hasText(value)) {
            MDC.put(key, value);
        }
    }
    
    /**
     * Get current request ID from MDC
     */
    public static String getCurrentRequestId() {
        return MDC.get(LoggingConstants.REQUEST_ID);
    }
    
    /**
     * Get current correlation ID from MDC
     */
    public static String getCurrentCorrelationId() {
        return MDC.get(LoggingConstants.CORRELATION_ID);
    }
    
    /**
     * Get current user ID from MDC
     */
    public static String getCurrentUserId() {
        return MDC.get(LoggingConstants.USER_ID);
    }
    
    /**
     * Get all current MDC properties
     */
    public static Map<String, String> getCurrentContext() {
        return MDC.getCopyOfContextMap();
    }
    
    /**
     * Save current context and return it
     */
    public static Map<String, String> saveContext() {
        return MDC.getCopyOfContextMap();
    }
    
    /**
     * Restore context from saved state
     */
    public static void restoreContext(Map<String, String> context) {
        MDC.clear();
        if (context != null) {
            MDC.setContextMap(context);
        }
    }
    
    /**
     * Clear all MDC context
     */
    public static void clearContext() {
        MDC.clear();
    }
    
    /**
     * Remove specific key from MDC
     */
    public static void removeProperty(String key) {
        MDC.remove(key);
    }
    
    /**
     * Generate a unique request ID
     */
    private static String generateRequestId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
} 
package com.library.user.infrastructure.logging;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.UUID;

/**
 * Utility class for managing MDC context for logging
 */
public final class MDCContextManager {

    public static final String REQUEST_ID = "requestId";
    public static final String CORRELATION_ID = "correlationId";
    public static final String SPAN_ID = "spanId";
    public static final String PARENT_SPAN_ID = "parentSpanId";
    public static final String USER_ID = "userId";
    public static final String CLIENT_IP = "clientIp";
    public static final String USER_AGENT = "userAgent";
    public static final String HTTP_METHOD = "httpMethod";
    public static final String REQUEST_URL = "requestUrl";
    public static final String CLASS_NAME = "className";
    public static final String METHOD_NAME = "methodName";
    public static final String EXECUTION_TIME = "executionTime";
    public static final String BUSINESS_LOGIC_STATUS = "businessLogicStatus";
    public static final String DATA_SOURCE = "dataSource";

    // Custom Tags
    public static final String SERVICE_NAME = "serviceName";
    public static final String OPERATION_TYPE = "operationType";
    public static final String RESOURCE_TYPE = "resourceType";
    public static final String ERROR_CODE = "errorCode";
    
    private MDCContextManager() {
        // Utility class
    }
    
    /**
     * Initialize MDC context with basic request information
     */
    public static void initializeContext() {
        String requestId = generateRequestId();
        MDC.put(REQUEST_ID, requestId);
        MDC.put(CORRELATION_ID, requestId); // Default correlation ID to request ID
    }
    
    /**
     * Initialize MDC context with provided correlation ID
     */
    public static void initializeContext(String correlationId) {
        String requestId = generateRequestId();
        MDC.put(REQUEST_ID, requestId);
        MDC.put(CORRELATION_ID, 
                StringUtils.hasText(correlationId) ? correlationId : requestId);
    }
    
    /**
     * Set HTTP request context information
     */
    public static void setHttpContext(String method, String url, String clientIp, String userAgent) {
        MDC.put(HTTP_METHOD, method);
        MDC.put(REQUEST_URL, url);
        MDC.put(CLIENT_IP, clientIp);
        MDC.put(USER_AGENT, userAgent);
    }
    
    /**
     * Set user context information
     */
    public static void setUserContext(String userId) {
        if (StringUtils.hasText(userId)) {
            MDC.put(USER_ID, userId);
        }
    }
    
    /**
     * Set method execution context
     */
    public static void setMethodContext(String className, String methodName) {
        MDC.put(CLASS_NAME, className);
        MDC.put(METHOD_NAME, methodName);
    }
    
    /**
     * Set execution time
     */
    public static void setExecutionTime(long executionTimeMs) {
        MDC.put(EXECUTION_TIME, String.valueOf(executionTimeMs));
    }
    
    /**
     * Set business logic status
     */
    public static void setBusinessLogicStatus(String status) {
        MDC.put(BUSINESS_LOGIC_STATUS, status);
    }
    
    /**
     * Set data source information
     */
    public static void setDataSource(String dataSource) {
        MDC.put(DATA_SOURCE, dataSource);
    }
    
    /**
     * Set service name
     */
    public static void setServiceName(String serviceName) {
        MDC.put(SERVICE_NAME, serviceName);
    }
    
    /**
     * Set operation type
     */
    public static void setOperationType(String operationType) {
        MDC.put(OPERATION_TYPE, operationType);
    }
    
    /**
     * Set resource type
     */
    public static void setResourceType(String resourceType) {
        MDC.put(RESOURCE_TYPE, resourceType);
    }
    
    /**
     * Set error code for error scenarios
     */
    public static void setErrorCode(String errorCode) {
        MDC.put(ERROR_CODE, errorCode);
    }
    
    /**
     * Set span ID for distributed tracing
     */
    public static void setSpanId(String spanId) {
        if (StringUtils.hasText(spanId)) {
            MDC.put(SPAN_ID, spanId);
        }
    }
    
    /**
     * Set parent span ID for distributed tracing
     */
    public static void setParentSpanId(String parentSpanId) {
        if (StringUtils.hasText(parentSpanId)) {
            MDC.put(PARENT_SPAN_ID, parentSpanId);
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
        return MDC.get(REQUEST_ID);
    }
    
    /**
     * Get current correlation ID from MDC
     */
    public static String getCurrentCorrelationId() {
        return MDC.get(CORRELATION_ID);
    }
    
    /**
     * Get current user ID from MDC
     */
    public static String getCurrentUserId() {
        return MDC.get(USER_ID);
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
package com.library.common.constants;

/**
 * Constants for logging system
 */
public final class LoggingConstants {
    
    private LoggingConstants() {
        // Utility class
    }
    
    // MDC Keys for contextual logging
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
    
    // Log Messages
    public static final String METHOD_ENTRY = "Method entered";
    public static final String METHOD_EXIT = "Method exited successfully";
    public static final String METHOD_ERROR = "Error occurred in method";
    public static final String REQUEST_RECEIVED = "HTTP request received";
    public static final String REQUEST_COMPLETED = "HTTP request completed";
    public static final String REQUEST_FAILED = "HTTP request failed";
    
    // Performance Thresholds (in milliseconds)
    public static final long SLOW_REQUEST_THRESHOLD = 1000L;
    public static final long VERY_SLOW_REQUEST_THRESHOLD = 5000L;
    
    // Sampling
    public static final String ENABLE_DETAILED_LOGGING_HEADER = "X-Enable-Detailed-Logging";
    public static final String SAMPLING_RATE_HEADER = "X-Sampling-Rate";
    
    // Sensitive Data Patterns (for masking)
    public static final String[] SENSITIVE_FIELDS = {
        "password", "token", "secret", "key", "credential", 
        "authorization", "authentication", "private", "confidential"
    };
} 
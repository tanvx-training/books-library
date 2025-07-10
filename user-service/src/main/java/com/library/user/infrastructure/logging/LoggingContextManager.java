package com.library.user.infrastructure.logging;

import com.library.book.infrastructure.enums.LogLevel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Manager class for handling logging context and sampling decisions
 */
public final class LoggingContextManager {

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
    private static final ThreadLocal<LogLevel> currentLogLevel = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> detailedLoggingEnabled = new ThreadLocal<>();
    private static final ThreadLocal<Double> currentSamplingRate = new ThreadLocal<>();
    
    private LoggingContextManager() {
        // Utility class
    }
    
    /**
     * Initialize logging context from HTTP request
     */
    public static void initializeFromRequest(HttpServletRequest request) {
        // Extract correlation ID from request headers
        String correlationId = extractCorrelationId(request);
        MDCContextManager.initializeContext(correlationId);
        
        // Set HTTP context
        MDCContextManager.setHttpContext(
            request.getMethod(),
            request.getRequestURL().toString(),
            getClientIpAddress(request),
            request.getHeader("User-Agent")
        );
        
        // Check for detailed logging enablement
        boolean detailedEnabled = isDetailedLoggingEnabled(request);
        setDetailedLoggingEnabled(detailedEnabled);
        
        // Set sampling rate
        double samplingRate = getSamplingRate(request);
        setSamplingRate(samplingRate);
        
        // Determine log level based on request
        LogLevel logLevel = determineLogLevel(request, detailedEnabled, samplingRate);
        setCurrentLogLevel(logLevel);
    }
    
    /**
     * Extract correlation ID from request headers
     */
    private static String extractCorrelationId(HttpServletRequest request) {
        // Try different header names for correlation ID
        String[] correlationHeaders = {
            "X-Correlation-ID", "X-Request-ID", "X-Trace-ID", 
            "Correlation-ID", "Request-ID", "Trace-ID"
        };
        
        for (String header : correlationHeaders) {
            String value = request.getHeader(header);
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        
        return null;
    }
    
    /**
     * Get client IP address from request
     */
    private static String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", 
            "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", 
            "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", 
            "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"
        };
        
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
                // Take the first IP in case of multiple IPs
                int commaIndex = ip.indexOf(',');
                return commaIndex > 0 ? ip.substring(0, commaIndex).trim() : ip;
            }
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Check if detailed logging is enabled for this request
     */
    private static boolean isDetailedLoggingEnabled(HttpServletRequest request) {
        String enableDetailedLogging = request.getHeader(ENABLE_DETAILED_LOGGING_HEADER);
        return "true".equalsIgnoreCase(enableDetailedLogging) || "1".equals(enableDetailedLogging);
    }
    
    /**
     * Get sampling rate from request headers
     */
    private static double getSamplingRate(HttpServletRequest request) {
        String samplingRateHeader = request.getHeader(SAMPLING_RATE_HEADER);
        if (StringUtils.hasText(samplingRateHeader)) {
            try {
                double rate = Double.parseDouble(samplingRateHeader);
                return Math.max(0.0, Math.min(1.0, rate)); // Clamp between 0 and 1
            } catch (NumberFormatException e) {
                // Invalid format, use default
            }
        }
        return 1.0; // Default to 100% sampling
    }
    
    /**
     * Determine appropriate log level based on request context
     */
    private static LogLevel determineLogLevel(HttpServletRequest request, 
                                            boolean detailedEnabled, 
                                            double samplingRate) {
        if (detailedEnabled) {
            return LogLevel.ADVANCED;
        }
        
        // Check if this request should be sampled for detailed logging
        if (shouldSample(samplingRate)) {
            return LogLevel.DETAILED;
        }
        
        return LogLevel.BASIC;
    }
    
    /**
     * Determine if this request should be sampled based on sampling rate
     */
    private static boolean shouldSample(double samplingRate) {
        return ThreadLocalRandom.current().nextDouble() < samplingRate;
    }
    
    /**
     * Set current log level for this thread
     */
    public static void setCurrentLogLevel(LogLevel logLevel) {
        currentLogLevel.set(logLevel);
    }
    
    /**
     * Get current log level for this thread
     */
    public static LogLevel getCurrentLogLevel() {
        LogLevel level = currentLogLevel.get();
        return level != null ? level : LogLevel.BASIC;
    }
    
    /**
     * Set detailed logging enabled flag
     */
    public static void setDetailedLoggingEnabled(boolean enabled) {
        detailedLoggingEnabled.set(enabled);
    }
    
    /**
     * Check if detailed logging is enabled
     */
    public static boolean isDetailedLoggingEnabled() {
        Boolean enabled = detailedLoggingEnabled.get();
        return enabled != null && enabled;
    }
    
    /**
     * Set current sampling rate
     */
    public static void setSamplingRate(double rate) {
        currentSamplingRate.set(rate);
    }
    
    /**
     * Get current sampling rate
     */
    public static double getCurrentSamplingRate() {
        Double rate = currentSamplingRate.get();
        return rate != null ? rate : 1.0;
    }
    
    /**
     * Check if logging should be performed based on current context
     */
    public static boolean shouldLog(LogLevel requiredLevel) {
        LogLevel currentLevel = getCurrentLogLevel();

        return switch (requiredLevel) {
            case BASIC -> currentLevel.includeBasicInfo();
            case DETAILED -> currentLevel.includeDetailedInfo();
            case ADVANCED -> currentLevel.includeAdvancedInfo();
        };
    }
    
    /**
     * Check if performance metrics should be logged
     */
    public static boolean shouldLogPerformanceMetrics() {
        return getCurrentLogLevel().includeDetailedInfo();
    }
    
    /**
     * Check if method arguments should be logged
     */
    public static boolean shouldLogMethodArguments() {
        return getCurrentLogLevel().includeDetailedInfo();
    }
    
    /**
     * Check if return values should be logged
     */
    public static boolean shouldLogReturnValues() {
        return getCurrentLogLevel().includeDetailedInfo();
    }
    
    /**
     * Check if advanced tracing should be enabled
     */
    public static boolean shouldEnableAdvancedTracing() {
        return getCurrentLogLevel().includeAdvancedInfo();
    }
    
    /**
     * Clear all thread-local variables
     */
    public static void clearContext() {
        currentLogLevel.remove();
        detailedLoggingEnabled.remove();
        currentSamplingRate.remove();
        MDCContextManager.clearContext();
    }
} 
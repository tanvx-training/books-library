package com.library.common.utils.logging;

import com.library.common.constants.LoggingConstants;
import com.library.common.enums.LogLevel;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Manager class for handling logging context and sampling decisions
 */
public final class LoggingContextManager {
    
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
        String enableDetailedLogging = request.getHeader(LoggingConstants.ENABLE_DETAILED_LOGGING_HEADER);
        return "true".equalsIgnoreCase(enableDetailedLogging) || "1".equals(enableDetailedLogging);
    }
    
    /**
     * Get sampling rate from request headers
     */
    private static double getSamplingRate(HttpServletRequest request) {
        String samplingRateHeader = request.getHeader(LoggingConstants.SAMPLING_RATE_HEADER);
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
        
        switch (requiredLevel) {
            case BASIC:
                return currentLevel.includeBasicInfo();
            case DETAILED:
                return currentLevel.includeDetailedInfo();
            case ADVANCED:
                return currentLevel.includeAdvancedInfo();
            default:
                return true;
        }
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
package com.library.dashboard.business.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class StructuredLogger {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public static void logOperation(Logger logger, String operation, String entityType, Object entityId, 
                                  String userId, boolean success, String message) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        logData.put("operation", operation);
        logData.put("entityType", entityType);
        logData.put("entityId", entityId);
        logData.put("userId", userId);
        logData.put("success", success);
        logData.put("message", message);

        String logMessage = formatLogMessage("OPERATION", logData);
        
        if (success) {
            logger.info(logMessage);
        } else {
            logger.warn(logMessage);
        }
    }

    public static void logOperationSuccess(Logger logger, String operation, String entityType, Object entityId, 
                                         String userId, String message) {
        logOperation(logger, operation, entityType, entityId, userId, true, message);
    }

    public static void logOperationFailure(Logger logger, String operation, String entityType, Object entityId, 
                                         String userId, String message) {
        logOperation(logger, operation, entityType, entityId, userId, false, message);
    }

    public static void logValidationError(Logger logger, String operation, String entityType, 
                                        Map<String, String> fieldErrors, String userId) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        logData.put("operation", operation);
        logData.put("entityType", entityType);
        logData.put("userId", userId);
        logData.put("errorType", "VALIDATION_ERROR");
        logData.put("fieldErrors", fieldErrors);

        String logMessage = formatLogMessage("VALIDATION_ERROR", logData);
        logger.warn(logMessage);
    }

    public static void logValidationError(Logger logger, String operation, String entityType, 
                                        String fieldName, Object fieldValue, String errorMessage, String userId) {
        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put(fieldName, errorMessage + " (value: " + fieldValue + ")");
        logValidationError(logger, operation, entityType, fieldErrors, userId);
    }

    public static void logDatabaseError(Logger logger, String operation, String entityType, Object entityId, 
                                      String userId, Exception exception, Map<String, Object> additionalContext) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        logData.put("operation", operation);
        logData.put("entityType", entityType);
        logData.put("entityId", entityId);
        logData.put("userId", userId);
        logData.put("errorType", "DATABASE_ERROR");
        logData.put("exceptionClass", exception.getClass().getSimpleName());
        logData.put("exceptionMessage", exception.getMessage());
        
        if (additionalContext != null) {
            logData.putAll(additionalContext);
        }

        String logMessage = formatLogMessage("DATABASE_ERROR", logData);
        logger.error(logMessage, exception);
    }

    public static void logDatabaseError(Logger logger, String operation, String entityType, Object entityId, 
                                      String userId, Exception exception) {
        logDatabaseError(logger, operation, entityType, entityId, userId, exception, null);
    }

    public static void logPerformanceMetrics(Logger logger, String operation, String entityType, 
                                           long duration, Integer recordCount, String userId) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        logData.put("operation", operation);
        logData.put("entityType", entityType);
        logData.put("userId", userId);
        logData.put("metricType", "PERFORMANCE");
        logData.put("durationMs", duration);
        
        if (recordCount != null) {
            logData.put("recordCount", recordCount);
        }

        String logMessage = formatLogMessage("PERFORMANCE_METRIC", logData);
        logger.info(logMessage);
    }

    private static String formatLogMessage(String logType, Map<String, Object> logData) {
        try {
            return String.format("[%s] %s", logType, objectMapper.writeValueAsString(logData));
        } catch (JsonProcessingException e) {
            // Fallback to simple string format if JSON serialization fails
            return String.format("[%s] %s", logType, logData.toString());
        }
    }
}
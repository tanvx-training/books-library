package com.library.catalog.business.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for structured logging with consistent format.
 * Provides methods for logging CRUD operations, validation errors, and database errors.
 */
public class StructuredLogger {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * Logs a CRUD operation with structured format.
     *
     * @param logger the logger instance
     * @param operation the operation type (CREATE, READ, UPDATE, DELETE)
     * @param entityType the entity type (e.g., AUTHOR)
     * @param entityId the entity ID (can be null for CREATE operations)
     * @param userId the user performing the operation
     * @param success whether the operation was successful
     * @param message additional message
     */
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

    /**
     * Logs a successful CRUD operation.
     *
     * @param logger the logger instance
     * @param operation the operation type
     * @param entityType the entity type
     * @param entityId the entity ID
     * @param userId the user performing the operation
     * @param message additional message
     */
    public static void logOperationSuccess(Logger logger, String operation, String entityType, Object entityId, 
                                         String userId, String message) {
        logOperation(logger, operation, entityType, entityId, userId, true, message);
    }

    /**
     * Logs a failed CRUD operation.
     *
     * @param logger the logger instance
     * @param operation the operation type
     * @param entityType the entity type
     * @param entityId the entity ID
     * @param userId the user performing the operation
     * @param message error message
     */
    public static void logOperationFailure(Logger logger, String operation, String entityType, Object entityId, 
                                         String userId, String message) {
        logOperation(logger, operation, entityType, entityId, userId, false, message);
    }

    /**
     * Logs validation errors with detailed field information.
     *
     * @param logger the logger instance
     * @param operation the operation that failed validation
     * @param entityType the entity type
     * @param fieldErrors map of field names to error messages
     * @param userId the user performing the operation
     */
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

    /**
     * Logs validation errors for a single field.
     *
     * @param logger the logger instance
     * @param operation the operation that failed validation
     * @param entityType the entity type
     * @param fieldName the field name
     * @param fieldValue the field value that failed validation
     * @param errorMessage the validation error message
     * @param userId the user performing the operation
     */
    public static void logValidationError(Logger logger, String operation, String entityType, 
                                        String fieldName, Object fieldValue, String errorMessage, String userId) {
        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put(fieldName, errorMessage + " (value: " + fieldValue + ")");
        logValidationError(logger, operation, entityType, fieldErrors, userId);
    }

    /**
     * Logs database errors with appropriate context.
     *
     * @param logger the logger instance
     * @param operation the operation that caused the database error
     * @param entityType the entity type
     * @param entityId the entity ID (can be null)
     * @param userId the user performing the operation
     * @param exception the database exception
     * @param additionalContext additional context information
     */
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

    /**
     * Logs database errors with minimal context.
     *
     * @param logger the logger instance
     * @param operation the operation that caused the database error
     * @param entityType the entity type
     * @param entityId the entity ID (can be null)
     * @param userId the user performing the operation
     * @param exception the database exception
     */
    public static void logDatabaseError(Logger logger, String operation, String entityType, Object entityId, 
                                      String userId, Exception exception) {
        logDatabaseError(logger, operation, entityType, entityId, userId, exception, null);
    }

    /**
     * Logs performance metrics for operations.
     *
     * @param logger the logger instance
     * @param operation the operation type
     * @param entityType the entity type
     * @param duration the operation duration in milliseconds
     * @param recordCount the number of records processed (optional)
     * @param userId the user performing the operation
     */
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

    /**
     * Formats log data into a structured JSON message.
     *
     * @param logType the type of log entry
     * @param logData the log data map
     * @return formatted log message
     */
    private static String formatLogMessage(String logType, Map<String, Object> logData) {
        try {
            return String.format("[%s] %s", logType, objectMapper.writeValueAsString(logData));
        } catch (JsonProcessingException e) {
            // Fallback to simple string format if JSON serialization fails
            return String.format("[%s] %s", logType, logData.toString());
        }
    }
}
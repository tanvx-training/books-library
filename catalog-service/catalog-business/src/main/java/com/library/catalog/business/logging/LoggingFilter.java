package com.library.catalog.business.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Aspect-based logging filter that automatically handles logging context
 * and structured logging for service operations.
 * 
 * This filter intercepts all service method calls and:
 * - Sets up logging context (MDC)
 * - Logs method entry and exit
 * - Handles exceptions with structured logging
 * - Measures execution time
 * - Cleans up context after execution
 */
@Aspect
@Component
@Order(1) // Execute before other aspects
public class LoggingFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    /**
     * Intercepts all methods in service implementation classes.
     * Automatically handles logging context setup, execution logging,
     * and cleanup.
     */
    @Around("execution(* com.library.catalog.business.impl.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        
        // Extract method information
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        // Determine operation details
        OperationContext context = extractOperationContext(methodName, args);
        
        // Set up logging context
        setupLoggingContext(requestId, context);
        
        try {
            // Log method entry
            logMethodEntry(className, methodName, context, args);
            
            // Execute the actual method
            Object result = joinPoint.proceed();
            
            // Calculate execution time
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Log successful completion
            logMethodSuccess(className, methodName, context, executionTime, result);
            
            return result;
            
        } catch (Exception e) {
            // Calculate execution time for failed operations
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Log method failure
            logMethodFailure(className, methodName, context, executionTime, e);
            
            // Re-throw the exception
            throw e;
            
        } finally {
            // Always clean up logging context
            LoggingContextManager.clearAllContext();
        }
    }

    /**
     * Extracts operation context from method name and arguments.
     */
    private OperationContext extractOperationContext(String methodName, Object[] args) {
        OperationContext context = new OperationContext();
        
        // Determine operation type from method name
        if (methodName.startsWith("create")) {
            context.operation = "CREATE";
            context.entityType = extractEntityType(methodName, "create");
        } else if (methodName.startsWith("get") || methodName.startsWith("find") || methodName.startsWith("search")) {
            context.operation = methodName.startsWith("search") ? "SEARCH" : 
                               methodName.contains("All") ? "READ_ALL" : "READ";
            context.entityType = extractEntityType(methodName, "get", "find", "search");
        } else if (methodName.startsWith("update")) {
            context.operation = "UPDATE";
            context.entityType = extractEntityType(methodName, "update");
        } else if (methodName.startsWith("delete")) {
            context.operation = "DELETE";
            context.entityType = extractEntityType(methodName, "delete");
        } else {
            context.operation = "UNKNOWN";
            context.entityType = "UNKNOWN";
        }
        
        // Extract entity ID and user from arguments
        context.entityId = extractEntityId(args);
        context.userId = extractUserId(args);
        
        return context;
    }

    /**
     * Extracts entity type from method name.
     */
    private String extractEntityType(String methodName, String... prefixes) {
        String remaining = methodName;
        
        // Remove known prefixes
        for (String prefix : prefixes) {
            if (remaining.toLowerCase().startsWith(prefix.toLowerCase())) {
                remaining = remaining.substring(prefix.length());
                break;
            }
        }
        
        // Extract entity name (e.g., "Author" from "createAuthor")
        if (!remaining.isEmpty()) {
            // Handle cases like "getAllAuthors" -> "AUTHOR"
            if (remaining.toLowerCase().startsWith("all")) {
                remaining = remaining.substring(3);
            }
            
            // Convert to singular and uppercase
            String entityType = remaining.replaceAll("s$", "").toUpperCase();
            return entityType.isEmpty() ? "UNKNOWN" : entityType;
        }
        
        return "UNKNOWN";
    }

    /**
     * Extracts entity ID from method arguments.
     * Assumes first Long/Integer argument is the entity ID.
     */
    private Object extractEntityId(Object[] args) {
        if (args != null) {
            for (Object arg : args) {
                if (arg instanceof Long || arg instanceof Integer) {
                    return arg;
                }
            }
        }
        return null;
    }

    /**
     * Extracts user ID from method arguments.
     * Assumes last String argument is the current user.
     */
    private String extractUserId(Object[] args) {
        if (args != null && args.length > 0) {
            // Look for currentUser parameter (usually last String parameter)
            for (int i = args.length - 1; i >= 0; i--) {
                if (args[i] instanceof String str) {
                    // Skip empty strings and common non-user parameters
                    if (!str.trim().isEmpty() && 
                        !str.toLowerCase().contains("search") && 
                        !str.toLowerCase().contains("name")) {
                        return str;
                    }
                }
            }
        }
        return "system"; // Default user
    }

    /**
     * Sets up logging context in MDC.
     */
    private void setupLoggingContext(String requestId, OperationContext context) {
        LoggingContextManager.setRequestContext(requestId);
        LoggingContextManager.setOperationContext(context.userId, context.operation, 
                                                context.entityType, context.entityId);
    }

    /**
     * Logs method entry with context information.
     */
    private void logMethodEntry(String className, String methodName, OperationContext context, Object[] args) {
        StructuredLogger.logOperationSuccess(logger, context.operation, context.entityType, 
            context.entityId, context.userId, 
            String.format("Starting %s.%s with %d arguments", className, methodName, 
                         args != null ? args.length : 0));
    }

    /**
     * Logs successful method completion.
     */
    private void logMethodSuccess(String className, String methodName, OperationContext context, 
                                long executionTime, Object result) {
        // Log performance metrics
        StructuredLogger.logPerformanceMetrics(logger, context.operation, context.entityType, 
                                             executionTime, getRecordCount(result), context.userId);
        
        // Log operation success
        StructuredLogger.logOperationSuccess(logger, context.operation, context.entityType, 
            context.entityId, context.userId, 
            String.format("Completed %s.%s successfully in %dms", className, methodName, executionTime));
    }

    /**
     * Logs method failure with exception details.
     */
    private void logMethodFailure(String className, String methodName, OperationContext context, 
                                long executionTime, Exception exception) {
        // Log performance metrics for failed operations
        StructuredLogger.logPerformanceMetrics(logger, context.operation, context.entityType, 
                                             executionTime, null, context.userId);
        
        // Log operation failure
        if (isValidationException(exception)) {
            StructuredLogger.logOperationFailure(logger, context.operation, context.entityType, 
                context.entityId, context.userId, 
                String.format("Validation failed in %s.%s: %s", className, methodName, exception.getMessage()));
        } else if (isDatabaseException(exception)) {
            StructuredLogger.logDatabaseError(logger, context.operation, context.entityType, 
                context.entityId, context.userId, exception);
        } else {
            StructuredLogger.logOperationFailure(logger, context.operation, context.entityType, 
                context.entityId, context.userId, 
                String.format("Failed %s.%s after %dms: %s", className, methodName, executionTime, exception.getMessage()));
        }
    }

    /**
     * Extracts record count from result object for performance logging.
     */
    private Integer getRecordCount(Object result) {
        if (result == null) return null;
        
        // Handle paged responses
        String resultClass = result.getClass().getSimpleName();
        if (resultClass.contains("Paged") || resultClass.contains("Page")) {
            try {
                Method getContentMethod = result.getClass().getMethod("getContent");
                Object content = getContentMethod.invoke(result);
                if (content instanceof java.util.Collection) {
                    return ((java.util.Collection<?>) content).size();
                }
            } catch (Exception e) {
                // Ignore reflection errors
            }
        }
        
        // Handle collections
        if (result instanceof java.util.Collection) {
            return ((java.util.Collection<?>) result).size();
        }
        
        // Single entity
        return 1;
    }

    /**
     * Checks if exception is a validation exception.
     */
    private boolean isValidationException(Exception exception) {
        String exceptionName = exception.getClass().getSimpleName().toLowerCase();
        return exceptionName.contains("validation") || 
               exceptionName.contains("illegal") ||
               exception instanceof IllegalArgumentException;
    }

    /**
     * Checks if exception is a database-related exception.
     */
    private boolean isDatabaseException(Exception exception) {
        String exceptionName = exception.getClass().getSimpleName().toLowerCase();
        return exceptionName.contains("sql") || 
               exceptionName.contains("database") ||
               exceptionName.contains("persistence") ||
               exceptionName.contains("jpa") ||
               exceptionName.contains("hibernate");
    }

    /**
     * Inner class to hold operation context information.
     */
    private static class OperationContext {
        String operation;
        String entityType;
        Object entityId;
        String userId;
    }
}
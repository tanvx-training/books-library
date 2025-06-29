package com.library.common.example;

import com.library.common.aop.annotation.Loggable;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import org.springframework.stereotype.Service;

/**
 * Example service demonstrating service-layer logging
 * 
 * This is for demonstration purposes only - remove in production
 */
@Service
public class LoggingExampleService {

    /**
     * Service method with detailed logging
     */
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.BUSINESS_LOGIC,
        logArguments = true,
        logReturnValue = true,
        resourceType = "ExampleResource"
    )
    public LoggingExampleController.ExampleDto processRequest(LoggingExampleController.ExampleDto request) {
        // Simulate some business logic
        String processedValue = "processed_" + request.getValue();
        
        return new LoggingExampleController.ExampleDto(
            request.getName(),
            "Processed: " + request.getDescription(),
            processedValue
        );
    }

    /**
     * Update operation with advanced logging
     */
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 200L,
        customTags = {"operation=update", "layer=service"}
    )
    public LoggingExampleController.ExampleDto updateResource(Long id, 
                                                              LoggingExampleController.ExampleDto request, 
                                                              boolean slowOperation) {
        // Simulate slow operation if requested
        if (slowOperation) {
            try {
                Thread.sleep(1000); // 1 second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Operation interrupted", e);
            }
        }

        return new LoggingExampleController.ExampleDto(
            "Updated_" + request.getName(),
            "Updated on ID: " + id + " - " + request.getDescription(),
            "updated_" + request.getValue()
        );
    }

    /**
     * Database operation simulation with repository-level logging
     */
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.DATABASE,
        logExecutionTime = true,
        customTags = {"datasource=postgresql", "table=example"}
    )
    public void simulateDatabaseOperation(String query, Object... parameters) {
        // Simulate database access time
        try {
            Thread.sleep(50); // 50ms database operation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Database operation interrupted", e);
        }
    }

    /**
     * Sensitive data processing with data sanitization
     */
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.AUTHENTICATION,
        sanitizeSensitiveData = true,
        logArguments = true,
        messagePrefix = "SENSITIVE_PROCESSING"
    )
    public String processSensitiveData(LoggingExampleController.SensitiveDataDto sensitiveData) {
        // Simulate authentication/authorization logic
        simulateDatabaseOperation("SELECT * FROM users WHERE username = ?", sensitiveData.getUsername());
        
        // Process data (password and token should be masked in logs)
        return "User authenticated: " + sensitiveData.getUsername();
    }

    /**
     * External API call simulation
     */
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.EXTERNAL_API,
        logExecutionTime = true,
        performanceThresholdMs = 300L,
        customTags = {"api=external-service", "timeout=5000"}
    )
    public String callExternalApi(String endpoint, Object payload) {
        // Simulate external API call
        try {
            Thread.sleep(200); // 200ms API call
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("External API call interrupted", e);
        }
        
        return "External API response from " + endpoint;
    }

    /**
     * Cache operation simulation
     */
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CACHE,
        logExecutionTime = true,
        customTags = {"cache=redis", "ttl=3600"}
    )
    public Object getCacheData(String key) {
        // Simulate cache lookup
        try {
            Thread.sleep(5); // 5ms cache operation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Cache operation interrupted", e);
        }
        
        return "Cached data for key: " + key;
    }

    /**
     * File operation simulation
     */
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.FILE_OPERATION,
        logExecutionTime = true,
        customTags = {"operation=read", "location=/tmp"}
    )
    public String readFile(String fileName) {
        // Simulate file reading
        try {
            Thread.sleep(100); // 100ms file operation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("File operation interrupted", e);
        }
        
        return "File content of " + fileName;
    }

    /**
     * Validation operation
     */
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.VALIDATION,
        logArguments = true,
        messagePrefix = "VALIDATION"
    )
    public boolean validateData(Object data) {
        // Simulate validation logic
        return data != null && !data.toString().isEmpty();
    }

    /**
     * Work simulation for performance testing
     */
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.OTHER,
        logExecutionTime = true
    )
    public void simulateWork(int delayMs) {
        if (delayMs > 0) {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Work simulation interrupted", e);
            }
        }
    }

    /**
     * Method without @Loggable annotation - will use default service-level logging when enabled
     */
    public String defaultServiceMethod(String input) {
        return "Default service processing: " + input;
    }

    /**
     * Method that throws exception for error logging demonstration
     */
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.BUSINESS_LOGIC,
        logException = true,
        messagePrefix = "ERROR_SERVICE"
    )
    public void methodThatThrowsException() {
        throw new IllegalArgumentException("This is a demo exception from service layer");
    }
} 
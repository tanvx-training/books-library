package com.library.common.example;

import com.library.common.aop.annotation.Loggable;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Example controller demonstrating different logging levels and configurations
 * 
 * This is for demonstration purposes only - remove in production
 */
@RestController
@RequestMapping("/api/logging-demo")
public class LoggingExampleController {

    @Autowired
    private LoggingExampleService loggingExampleService;

    /**
     * Basic logging example - uses default @Loggable configuration
     */
    @GetMapping("/basic")
    @Loggable(level = LogLevel.BASIC, operationType = OperationType.READ)
    public ResponseEntity<String> basicLoggingExample() {
        return ResponseEntity.ok("Basic logging example completed");
    }

    /**
     * Detailed logging example with method arguments and return values
     */
    @PostMapping("/detailed")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.CREATE,
        resourceType = "ExampleResource",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        messagePrefix = "DETAILED_DEMO"
    )
    public ResponseEntity<ExampleDto> detailedLoggingExample(@RequestBody ExampleDto request) {
        ExampleDto response = loggingExampleService.processRequest(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Advanced logging with performance monitoring and custom tags
     */
    @PutMapping("/advanced/{id}")
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "ExampleResource",
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 500L,
        customTags = {"feature=advanced-demo", "version=1.0", "critical=true"}
    )
    public ResponseEntity<ExampleDto> advancedLoggingExample(
            @PathVariable Long id, 
            @RequestBody ExampleDto request,
            @RequestParam(defaultValue = "false") boolean slowOperation) {
        
        ExampleDto response = loggingExampleService.updateResource(id, request, slowOperation);
        return ResponseEntity.ok(response);
    }

    /**
     * Error handling demonstration
     */
    @GetMapping("/error")
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        logException = true,
        messagePrefix = "ERROR_DEMO"
    )
    public ResponseEntity<String> errorExample(@RequestParam(defaultValue = "false") boolean triggerError) {
        if (triggerError) {
            throw new RuntimeException("This is a demo exception for logging");
        }
        return ResponseEntity.ok("No error occurred");
    }

    /**
     * Sensitive data handling demonstration
     */
    @PostMapping("/sensitive")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.AUTHENTICATION,
        sanitizeSensitiveData = true,
        logArguments = true,
        messagePrefix = "SENSITIVE_DEMO"
    )
    public ResponseEntity<String> sensitiveDataExample(@RequestBody SensitiveDataDto sensitiveData) {
        // This will demonstrate how sensitive data is masked in logs
        loggingExampleService.processSensitiveData(sensitiveData);
        return ResponseEntity.ok("Sensitive data processed successfully");
    }

    /**
     * Method without @Loggable annotation - will use default controller logging
     */
    @GetMapping("/default")
    public ResponseEntity<String> defaultLoggingExample() {
        // This method will be logged with default controller-level logging
        return ResponseEntity.ok("Default controller logging example");
    }

    /**
     * Performance monitoring demonstration
     */
    @GetMapping("/performance")
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.BUSINESS_LOGIC,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 100L,
        messagePrefix = "PERF_DEMO"
    )
    public ResponseEntity<String> performanceExample(@RequestParam(defaultValue = "0") int delayMs) {
        loggingExampleService.simulateWork(delayMs);
        return ResponseEntity.ok("Performance demo completed");
    }

    // DTOs for demonstration
    public static class ExampleDto {
        private String name;
        private String description;
        private String value;

        // Constructors
        public ExampleDto() {}

        public ExampleDto(String name, String description, String value) {
            this.name = name;
            this.description = description;
            this.value = value;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        @Override
        public String toString() {
            return "ExampleDto{name='" + name + "', description='" + description + "', value='" + value + "'}";
        }
    }

    public static class SensitiveDataDto {
        private String username;
        private String password;  // This will be masked in logs
        private String token;     // This will be masked in logs
        private String email;
        private String publicInfo;

        // Constructors
        public SensitiveDataDto() {}

        public SensitiveDataDto(String username, String password, String token, String email, String publicInfo) {
            this.username = username;
            this.password = password;
            this.token = token;
            this.email = email;
            this.publicInfo = publicInfo;
        }

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPublicInfo() { return publicInfo; }
        public void setPublicInfo(String publicInfo) { this.publicInfo = publicInfo; }

        @Override
        public String toString() {
            return "SensitiveDataDto{username='" + username + "', password='***', token='***', email='" + email + "', publicInfo='" + publicInfo + "'}";
        }
    }
} 
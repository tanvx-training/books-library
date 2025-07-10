package com.library.user.infrastructure.logging;

import com.library.book.infrastructure.enums.LogLevel;
import com.library.book.infrastructure.enums.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable automatic logging for methods
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    
    /**
     * Minimum log level required for this method to be logged
     */
    LogLevel level() default LogLevel.BASIC;
    
    /**
     * Operation type for categorizing the log
     */
    OperationType operationType() default OperationType.OTHER;
    
    /**
     * Whether to log method arguments
     */
    boolean logArguments() default true;
    
    /**
     * Whether to log return value
     */
    boolean logReturnValue() default true;
    
    /**
     * Whether to log execution time
     */
    boolean logExecutionTime() default true;
    
    /**
     * Whether to log on method entry
     */
    boolean logEntry() default true;
    
    /**
     * Whether to log on successful method exit
     */
    boolean logExit() default true;
    
    /**
     * Whether to log on exception
     */
    boolean logException() default true;
    
    /**
     * Custom message prefix for logs
     */
    String messagePrefix() default "";
    
    /**
     * Resource type being operated on (for business context)
     */
    String resourceType() default "";
    
    /**
     * Whether to include this method in performance monitoring
     */
    boolean includeInPerformanceMonitoring() default false;
    
    /**
     * Performance threshold in milliseconds - log as WARN if exceeded
     */
    long performanceThresholdMs() default 1000L;
    
    /**
     * Whether to sanitize sensitive data in arguments and return values
     */
    boolean sanitizeSensitiveData() default true;
    
    /**
     * Custom tags to add to the log context
     */
    String[] customTags() default {};
} 
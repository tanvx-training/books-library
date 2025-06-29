package com.library.common.aop.logging;

import com.library.common.aop.annotation.Loggable;
import com.library.common.constants.LoggingConstants;
import com.library.common.enums.LogLevel;
import com.library.common.utils.logging.DataSanitizer;
import com.library.common.utils.logging.LoggingContextManager;
import com.library.common.utils.logging.MDCContextManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

/**
 * Main AOP Aspect for method logging across all application layers
 */
@Aspect
@Component
@Order(1)
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Pointcut for methods annotated with @Loggable
     */
    @Pointcut("@annotation(com.library.common.aop.annotation.Loggable)")
    public void loggableMethod() {}

    /**
     * Pointcut for all methods in Controller classes
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || " +
              "within(@org.springframework.stereotype.Controller *)")
    public void controllerMethods() {}

    /**
     * Pointcut for all methods in Service classes
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    /**
     * Pointcut for all methods in Repository classes
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repositoryMethods() {}

    /**
     * Around advice for methods with @Loggable annotation
     */
    @Around("loggableMethod() && @annotation(loggable)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
        return executeWithLogging(joinPoint, loggable);
    }

    /**
     * Around advice for controller methods (basic logging)
     */
    @Around("controllerMethods() && !loggableMethod()")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Loggable defaultLoggable = createDefaultLoggable(LogLevel.BASIC);
        return executeWithLogging(joinPoint, defaultLoggable);
    }

    /**
     * Around advice for service methods (detailed logging when enabled)
     */
    @Around("serviceMethods() && !loggableMethod()")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        if (LoggingContextManager.shouldLog(LogLevel.DETAILED)) {
            Loggable defaultLoggable = createDefaultLoggable(LogLevel.DETAILED);
            return executeWithLogging(joinPoint, defaultLoggable);
        }
        return joinPoint.proceed();
    }

    /**
     * Around advice for repository methods (advanced logging when enabled)
     */
    @Around("repositoryMethods() && !loggableMethod()")
    public Object logRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        if (LoggingContextManager.shouldLog(LogLevel.ADVANCED)) {
            Loggable defaultLoggable = createDefaultLoggable(LogLevel.ADVANCED);
            return executeWithLogging(joinPoint, defaultLoggable);
        }
        return joinPoint.proceed();
    }

    /**
     * Exception handling for all monitored methods
     */
    @AfterThrowing(pointcut = "controllerMethods() || serviceMethods() || repositoryMethods() || loggableMethod()", 
                   throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = method.getName();

            // Set method context in MDC
            MDCContextManager.setMethodContext(className, methodName);
            MDCContextManager.setBusinessLogicStatus("FAILURE");

            // Get error code if it's a custom exception
            String errorCode = extractErrorCode(exception);
            if (StringUtils.hasText(errorCode)) {
                MDCContextManager.setErrorCode(errorCode);
            }

            // Log the exception
            if (LoggingContextManager.shouldLog(LogLevel.BASIC)) {
                logger.error(LoggingConstants.METHOD_ERROR + " in {}.{}: {}", 
                    className, methodName, DataSanitizer.sanitizeException(exception), exception);
            }

        } catch (Exception e) {
            // Don't let logging errors affect the main application flow
            logger.warn("Error in logging aspect exception handler", e);
        }
    }

    /**
     * Core method execution with comprehensive logging
     */
    private Object executeWithLogging(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        
        // Generate span ID for tracing if advanced logging is enabled
        String spanId = null;
        if (LoggingContextManager.shouldEnableAdvancedTracing()) {
            spanId = generateSpanId();
            MDCContextManager.setSpanId(spanId);
        }

        // Set method context
        MDCContextManager.setMethodContext(className, methodName);
        MDCContextManager.setOperationType(loggable.operationType().getCode());
        
        if (StringUtils.hasText(loggable.resourceType())) {
            MDCContextManager.setResourceType(loggable.resourceType());
        }

        // Add custom tags
        addCustomTags(loggable.customTags());

        Logger methodLogger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        Object result = null;
        Throwable thrownException = null;

        try {
            // Log method entry
            if (loggable.logEntry() && LoggingContextManager.shouldLog(loggable.level())) {
                logMethodEntry(methodLogger, className, methodName, joinPoint.getArgs(), loggable);
            }

            // Execute the method
            result = joinPoint.proceed();

            // Log successful execution
            if (loggable.logExit() && LoggingContextManager.shouldLog(loggable.level())) {
                logMethodExit(methodLogger, className, methodName, result, loggable);
            }

            MDCContextManager.setBusinessLogicStatus("SUCCESS");
            return result;

        } catch (Throwable throwable) {
            thrownException = throwable;
            MDCContextManager.setBusinessLogicStatus("FAILURE");

            if (loggable.logException()) {
                String errorCode = extractErrorCode(throwable);
                if (StringUtils.hasText(errorCode)) {
                    MDCContextManager.setErrorCode(errorCode);
                }

                methodLogger.error(LoggingConstants.METHOD_ERROR + " in {}.{}: {}", 
                    className, methodName, DataSanitizer.sanitizeException(throwable), throwable);
            }

            throw throwable;

        } finally {
            // Log execution time
            long executionTime = System.currentTimeMillis() - startTime;
            MDCContextManager.setExecutionTime(executionTime);

            if (loggable.logExecutionTime() && LoggingContextManager.shouldLogPerformanceMetrics()) {
                logExecutionTime(methodLogger, className, methodName, executionTime, 
                               loggable.performanceThresholdMs(), thrownException == null);
            }

            // Performance monitoring
            if (loggable.includeInPerformanceMonitoring()) {
                logPerformanceMetrics(methodLogger, className, methodName, executionTime, 
                                    loggable.performanceThresholdMs());
            }

            // Clean up span ID
            if (spanId != null) {
                MDCContextManager.removeProperty(LoggingConstants.SPAN_ID);
            }
        }
    }

    /**
     * Log method entry with arguments
     */
    private void logMethodEntry(Logger methodLogger, String className, String methodName, 
                               Object[] args, Loggable loggable) {
        try {
            if (loggable.logArguments() && LoggingContextManager.shouldLogMethodArguments()) {
                Object[] sanitizedArgs = loggable.sanitizeSensitiveData() ? 
                    DataSanitizer.sanitizeMethodArguments(args) : args;
                
                methodLogger.info("{} - {}.{} with args: {}", 
                    getLogMessage(loggable, LoggingConstants.METHOD_ENTRY), 
                    className, methodName, Arrays.toString(sanitizedArgs));
            } else {
                methodLogger.info("{} - {}.{}", 
                    getLogMessage(loggable, LoggingConstants.METHOD_ENTRY), 
                    className, methodName);
            }
        } catch (Exception e) {
            methodLogger.debug("Error logging method entry for {}.{}", className, methodName, e);
        }
    }

    /**
     * Log method exit with return value
     */
    private void logMethodExit(Logger methodLogger, String className, String methodName, 
                              Object result, Loggable loggable) {
        try {
            if (loggable.logReturnValue() && LoggingContextManager.shouldLogReturnValues()) {
                Object sanitizedResult = loggable.sanitizeSensitiveData() ? 
                    DataSanitizer.sanitizeForLogging(result) : result;
                
                methodLogger.info("{} - {}.{} returned: {}", 
                    getLogMessage(loggable, LoggingConstants.METHOD_EXIT), 
                    className, methodName, sanitizedResult);
            } else {
                methodLogger.info("{} - {}.{}", 
                    getLogMessage(loggable, LoggingConstants.METHOD_EXIT), 
                    className, methodName);
            }
        } catch (Exception e) {
            methodLogger.debug("Error logging method exit for {}.{}", className, methodName, e);
        }
    }

    /**
     * Log execution time with performance analysis
     */
    private void logExecutionTime(Logger methodLogger, String className, String methodName, 
                                long executionTime, long threshold, boolean successful) {
        try {
            if (executionTime > LoggingConstants.VERY_SLOW_REQUEST_THRESHOLD) {
                methodLogger.warn("VERY SLOW execution: {}.{} took {}ms (threshold: {}ms) - Status: {}", 
                    className, methodName, executionTime, threshold, successful ? "SUCCESS" : "FAILED");
            } else if (executionTime > threshold) {
                methodLogger.warn("SLOW execution: {}.{} took {}ms (threshold: {}ms) - Status: {}", 
                    className, methodName, executionTime, threshold, successful ? "SUCCESS" : "FAILED");
            } else {
                methodLogger.debug("Execution time: {}.{} took {}ms - Status: {}", 
                    className, methodName, executionTime, successful ? "SUCCESS" : "FAILED");
            }
        } catch (Exception e) {
            methodLogger.debug("Error logging execution time for {}.{}", className, methodName, e);
        }
    }

    /**
     * Log performance metrics for monitoring
     */
    private void logPerformanceMetrics(Logger methodLogger, String className, String methodName, 
                                     long executionTime, long threshold) {
        try {
            methodLogger.info("PERFORMANCE_METRIC: method={}.{}, executionTime={}ms, threshold={}ms, " +
                "exceeded={}, requestId={}, correlationId={}", 
                className, methodName, executionTime, threshold, 
                executionTime > threshold,
                MDCContextManager.getCurrentRequestId(),
                MDCContextManager.getCurrentCorrelationId());
        } catch (Exception e) {
            methodLogger.debug("Error logging performance metrics for {}.{}", className, methodName, e);
        }
    }

    /**
     * Add custom tags to MDC context
     */
    private void addCustomTags(String[] customTags) {
        if (customTags != null && customTags.length > 0) {
            for (String tag : customTags) {
                if (StringUtils.hasText(tag) && tag.contains("=")) {
                    String[] keyValue = tag.split("=", 2);
                    if (keyValue.length == 2) {
                        MDCContextManager.addCustomProperty(keyValue[0].trim(), keyValue[1].trim());
                    }
                }
            }
        }
    }

    /**
     * Extract error code from exception
     */
    private String extractErrorCode(Throwable throwable) {
        // Try to extract error code from custom exceptions
        String className = throwable.getClass().getSimpleName();
        if (className.contains("Exception")) {
            return className.replace("Exception", "").toUpperCase();
        }
        return throwable.getClass().getSimpleName();
    }

    /**
     * Get log message with custom prefix
     */
    private String getLogMessage(Loggable loggable, String defaultMessage) {
        return StringUtils.hasText(loggable.messagePrefix()) ? 
            loggable.messagePrefix() + " - " + defaultMessage : defaultMessage;
    }

    /**
     * Generate unique span ID for distributed tracing
     */
    private String generateSpanId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * Create default loggable configuration
     */
    private Loggable createDefaultLoggable(LogLevel level) {
        return new Loggable() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Loggable.class;
            }

            @Override
            public LogLevel level() { return level; }

            @Override
            public com.library.common.enums.OperationType operationType() { 
                return com.library.common.enums.OperationType.OTHER; 
            }

            @Override
            public boolean logArguments() { 
                return level == LogLevel.DETAILED || level == LogLevel.ADVANCED; 
            }

            @Override
            public boolean logReturnValue() { 
                return level == LogLevel.DETAILED || level == LogLevel.ADVANCED; 
            }

            @Override
            public boolean logExecutionTime() { return true; }
            @Override
            public boolean logEntry() { return true; }
            @Override
            public boolean logExit() { return true; }
            @Override
            public boolean logException() { return true; }
            @Override
            public String messagePrefix() { return ""; }
            @Override
            public String resourceType() { return ""; }
            @Override
            public boolean includeInPerformanceMonitoring() { return false; }
            @Override
            public long performanceThresholdMs() { return LoggingConstants.SLOW_REQUEST_THRESHOLD; }
            @Override
            public boolean sanitizeSensitiveData() { return true; }
            @Override
            public String[] customTags() { return new String[0]; }
        };
    }
} 
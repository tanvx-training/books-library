package com.library.member.business.logging;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration class for enabling aspect-based logging in the service layer.
 * 
 * This configuration:
 * - Enables AspectJ auto proxy for AOP functionality
 * - Configures the logging filter to intercept service method calls
 * - Ensures proper order of aspect execution
 */
@Configuration
@EnableAspectJAutoProxy
public class ServiceLoggingConfig {
    
    // Configuration is handled through annotations
    // The LoggingFilter aspect will be automatically detected and applied
}
package com.library.member.business.logging;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class ServiceLoggingConfig {
    
    // Configuration is handled through annotations
    // The LoggingFilter aspect will be automatically detected and applied
}
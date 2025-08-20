package com.library.catalog.framework.secutiry;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class DatabaseConfig {
    
    // Configuration is handled through annotations and application properties
    // Additional beans can be added here if needed for custom configurations
}
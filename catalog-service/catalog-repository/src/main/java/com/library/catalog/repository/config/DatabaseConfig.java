package com.library.catalog.repository.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database configuration class for JPA settings.
 * Configures entity scanning, repository scanning, JPA auditing, and transaction management.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.library.catalog.repository")
@EntityScan(basePackages = "com.library.catalog.repository.entity")
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {
    
    // Configuration is handled through annotations and application properties
    // Additional beans can be added here if needed for custom configurations
}
package com.library.member.repository.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.library.member.repository")
@EntityScan(basePackages = "com.library.member.repository.entity")
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {
    
    // Configuration is handled through annotations and application properties
    // Additional beans can be added here if needed for custom configurations
}
package com.library.book.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
@EntityScan(basePackages = "com.library.book.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "com.library.book.infrastructure.persistence.repository")
public class PersistenceConfig {
    // Configuration for persistence layer
} 
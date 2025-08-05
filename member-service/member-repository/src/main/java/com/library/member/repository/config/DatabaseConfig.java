package com.library.member.repository.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.library.member.repository")
@EntityScan(basePackages = "com.library.member.repository.entity")
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableTransactionManagement
public class DatabaseConfig {
    
}
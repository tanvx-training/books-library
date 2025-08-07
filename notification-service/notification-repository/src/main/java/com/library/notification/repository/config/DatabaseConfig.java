package com.library.notification.repository.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.library.notification.repository")
@EntityScan(basePackages = "com.library.notification.repository.entity")
@EnableTransactionManagement
public class DatabaseConfig {

}
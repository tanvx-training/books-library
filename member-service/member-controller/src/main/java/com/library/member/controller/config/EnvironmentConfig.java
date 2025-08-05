package com.library.member.controller.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@Slf4j
public class EnvironmentConfig {

    @PostConstruct
    public void loadEnvironmentVariables() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .filename(".env")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            // Set system properties from .env file
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                
                // Only set if not already defined as system property or environment variable
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                    log.debug("Loaded environment variable from .env: {}", key);
                }
            });

            log.info("Environment configuration loaded successfully from .env file");

        } catch (Exception e) {
            log.warn("Could not load .env file: {}. Using system environment variables.", e.getMessage());
        }
    }
}
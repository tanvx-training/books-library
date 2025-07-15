package com.library.user.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for Keycloak integration
 */
@Configuration
public class KeycloakConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
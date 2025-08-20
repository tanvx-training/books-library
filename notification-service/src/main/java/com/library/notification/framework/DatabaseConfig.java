package com.library.notification.framework;

import com.library.notification.aop.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class DatabaseConfig {
    
    @Bean
    public AuditorAware<String> auditorProvider(AuditorAwareImpl auditorAware) {
        return auditorAware;
    }
}

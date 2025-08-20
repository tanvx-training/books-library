package com.library.member.framework;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF as we're using JWT tokens
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configure session management to be stateless
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configure authorization rules
            .authorizeHttpRequests(authz -> authz
                // Allow access to health check and actuator endpoints
                .requestMatchers("/actuator/**", "/health", "/info").permitAll()
                
                // Allow access to API documentation
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                
                // All API endpoints require authentication
                .requestMatchers("/api/**").authenticated()
                
                // Allow all other requests (for flexibility)
                .anyRequest().permitAll()
            )
            
            // Configure OAuth2 resource server with JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> {
                    // JWT decoder will be auto-configured from application properties
                    // spring.security.oauth2.resourceserver.jwt.issuer-uri
                    // spring.security.oauth2.resourceserver.jwt.jwk-set-uri
                })
            );

        return http.build();
    }
}
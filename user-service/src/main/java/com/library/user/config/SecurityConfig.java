package com.library.user.config;

import com.library.user.aop.filter.JwtAuthenticationFilter;
import com.library.user.utils.constant.SecurityConstants;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configure security
        http
                // Disable CSRF as we are using stateless JWT authentication
                .csrf(AbstractHttpConfigurer::disable)
                // Use stateless session management
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configure authorization
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints that don't require authentication
                        .requestMatchers(
                                SecurityConstants.AUTH_LOGIN_URL,
                                SecurityConstants.AUTH_REGISTER_URL,
                                SecurityConstants.AUTH_REFRESH_URL,
                                SecurityConstants.AUTH_JWKS_URL
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST ,"/api/users/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET ,"/api/users/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers("/api/library-cards/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN")
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Authentication Error\", \"message\": \"" + authException.getMessage() + "\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 status
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\": \"Access Denied\", \"message\": \"" + accessDeniedException.getMessage() + "\"}"
                            );
                        })
                )
                .authenticationManager(authenticationManager(http))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Return the built configuration
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}
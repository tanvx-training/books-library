package com.library.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import com.library.common.aop.filter.LoggingFilter;

/**
 * Configuration class for logging system
 */
@Configuration
@EnableAspectJAutoProxy
public class LoggingConfig {

    /**
     * Register LoggingFilter with specific order and URL patterns
     */
    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        
        registrationBean.setFilter(new LoggingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        registrationBean.setName("loggingFilter");
        
        return registrationBean;
    }
} 
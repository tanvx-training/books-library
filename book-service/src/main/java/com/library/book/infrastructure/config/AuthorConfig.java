package com.library.book.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class AuthorConfig {

    // Cấu hình để xử lý Domain Events bất đồng bộ
    @Bean(name = "domainEventMulticaster")
    public ApplicationEventMulticaster domainEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor("domain-events-"));
        return eventMulticaster;
    }
}

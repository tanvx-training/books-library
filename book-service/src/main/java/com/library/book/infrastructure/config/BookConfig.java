package com.library.book.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class BookConfig {

    // Configuration for handling Domain Events asynchronously
    @Bean(name = "bookDomainEventMulticaster")
    public ApplicationEventMulticaster bookDomainEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor("book-domain-events-"));
        return eventMulticaster;
    }
} 
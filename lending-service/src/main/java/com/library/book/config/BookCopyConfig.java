package com.library.book.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.library.book.domain",
    "com.library.book.application",
    "com.library.book.infrastructure",
    "com.library.book.interfaces"
})
public class BookCopyConfig {
    // Configuration for BookCopy module
} 
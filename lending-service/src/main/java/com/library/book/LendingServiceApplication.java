package com.library.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.library.book.domain",
    "com.library.book.application",
    "com.library.book.infrastructure",
    "com.library.book.interfaces",
    "com.library.book.config",
    "com.library.common"
})
public class LendingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LendingServiceApplication.class, args);
    }

}

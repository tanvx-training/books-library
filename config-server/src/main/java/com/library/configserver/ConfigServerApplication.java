package com.library.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {

    public static void main(String[] args) {
        // Disable config client for config server
        System.setProperty("spring.cloud.config.enabled", "false");
        System.setProperty("spring.cloud.config.import-check.enabled", "false");
        SpringApplication.run(ConfigServerApplication.class, args);
    }

}

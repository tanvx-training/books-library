package com.library.book.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 3 Configuration for Book Service
 * Provides comprehensive API documentation with security integration
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Value("${spring.application.name:book-service}")
    private String applicationName;

    @Bean
    public OpenAPI bookServiceOpenAPI() {
        return new OpenAPI()
                .openapi("3.0.3")
                .info(createApiInfo())
                .servers(createServers())
                .addSecurityItem(createSecurityRequirement())
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication", createSecurityScheme()))
                .tags(createTags());
    }

    private Info createApiInfo() {
        return new Info()
                .title("Library Management System - Book Service API")
                .description("""
                        ## Book Service API Documentation
                        
                        This service manages books, authors, publishers, categories, and book copies in the library system.
                        
                        ### Features:
                        - **Book Management**: CRUD operations for books with rich metadata
                        - **Author Management**: Author profiles and book associations
                        - **Publisher Management**: Publisher information and book relationships
                        - **Category Management**: Hierarchical book categorization
                        - **Book Copy Management**: Physical book copy tracking and lending
                        - **Advanced Search**: Multi-criteria book search functionality
                        - **User Context**: Integration with Keycloak for user authentication
                        
                        ### Authentication:
                        All write operations require valid JWT token from Keycloak.
                        
                        ### Rate Limiting:
                        API calls are rate-limited to ensure system stability.
                        
                        ### Error Handling:
                        All endpoints return standardized error responses with detailed messages.
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Library Development Team")
                        .email("dev@library.com")
                        .url("https://library.com/contact"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> createServers() {
        return List.of(
                new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Local Development Server"),
                new Server()
                        .url("https://api-dev.library.com")
                        .description("Development Environment"),
                new Server()
                        .url("https://api-staging.library.com")
                        .description("Staging Environment"),
                new Server()
                        .url("https://api.library.com")
                        .description("Production Environment")
        );
    }

    private SecurityRequirement createSecurityRequirement() {
        return new SecurityRequirement().addList("Bearer Authentication");
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name("Bearer Authentication")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter JWT token obtained from Keycloak authentication");
    }

    private List<Tag> createTags() {
        return List.of(
                new Tag()
                        .name("Books")
                        .description("Book management operations - Create, read, update, delete books with comprehensive metadata"),
                new Tag()
                        .name("Authors")
                        .description("Author management operations - Manage author profiles and their book associations"),
                new Tag()
                        .name("Publishers")
                        .description("Publisher management operations - Handle publisher information and book relationships"),
                new Tag()
                        .name("Categories")
                        .description("Category management operations - Organize books into hierarchical categories"),
                new Tag()
                        .name("Book Copies")
                        .description("Physical book copy management - Track individual book copies, lending, and reservations"),
                new Tag()
                        .name("Search")
                        .description("Advanced search operations - Multi-criteria book search with filtering and sorting")
        );
    }
}
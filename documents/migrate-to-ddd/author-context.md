# Migrate Module Author sang Domain-Driven Design (DDD)

## Mục lục
1. [Giới thiệu](#giới-thiệu)
2. [Cấu trúc thư mục](#cấu-trúc-thư-mục)
3. [Domain Layer](#domain-layer)
4. [Infrastructure Layer](#infrastructure-layer)
5. [Application Layer](#application-layer)
6. [Interface Layer](#interface-layer)
7. [Exception Handling](#exception-handling)
8. [Testing với cURL](#testing-với-curl)

## Giới thiệu

Tài liệu này mô tả chi tiết quá trình migrate module Author từ kiến trúc truyền thống sang Domain-Driven Design (DDD). Quá trình này bao gồm việc tái cấu trúc code, tạo các domain models phong phú, và triển khai các patterns của DDD.

## Cấu trúc thư mục

```
book-service/
└── src/main/java/com/library/book/
├── application/
│   ├── service/
│   │   └── AuthorApplicationService.java
│   ├── exception/
│   │   ├── AuthorApplicationException.java
│   │   └── AuthorNotFoundException.java
│   └── dto/
│       ├── request/
│       │   └── AuthorCreateRequest.java
│       └── response/
│           └── AuthorResponse.java
├── domain/
│   ├── model/
│   │   ├── author/
│   │   │   ├── Author.java
│   │   │   ├── AuthorId.java
│   │   │   ├── AuthorName.java
│   │   │   └── Biography.java
│   │   └── shared/
│   │       ├── AggregateRoot.java
│   │       └── DomainEvent.java
│   ├── repository/
│   │   └── AuthorRepository.java
│   ├── service/
│   │   └── AuthorDomainService.java
│   ├── exception/
│   │   ├── AuthorDomainException.java
│   │   ├── DomainException.java
│   │   └── InvalidAuthorDataException.java
│   └── event/
│       └── AuthorCreatedEvent.java
├── infrastructure/
│   ├── persistence/
│   │   ├── entity/
│   │   │   └── AuthorJpaEntity.java
│   │   ├── mapper/
│   │   │   └── AuthorEntityMapper.java
│   │   ├── repository/
│   │   │   └── AuthorJpaRepository.java
│   │   └── impl/
│   │       └── AuthorRepositoryImpl.java
│   ├── exception/
│   │   └── AuthorPersistenceException.java
│   └── config/
│       └── AuthorConfig.java
└── interfaces/
├── rest/
│   └── AuthorController.java
└── rest/exception/
├── AuthorExceptionHandler.java
└── DddGlobalExceptionHandler.java
```

## Domain Layer

### AggregateRoot.java
```java
package com.library.book.domain.model.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot {
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }
    
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    public void clearEvents() {
        domainEvents.clear();
    }
}
```

### DomainEvent.java
```java
package com.library.book.domain.model.shared;

import java.time.LocalDateTime;

public abstract class DomainEvent {
    private final LocalDateTime occurredOn;
    
    protected DomainEvent() {
        this.occurredOn = LocalDateTime.now();
    }
    
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
```

### AuthorId.java
```java
package com.library.book.domain.model.author;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class AuthorId implements Serializable {
    private Long value;
    
    public AuthorId(Long value) {
        this.value = value;
    }
    
    public static AuthorId createNew() {
        return new AuthorId(null); // ID sẽ được tạo bởi DB
    }
    
    @Override
    public String toString() {
        return value != null ? value.toString() : "new";
    }
}
```

### AuthorName.java
```java
package com.library.book.domain.model.author;

import com.library.book.domain.exception.InvalidAuthorDataException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class AuthorName {
    private String value;
    
    private AuthorName(String value) {
        this.value = value;
    }
    
    public static AuthorName of(String name) {
        validate(name);
        return new AuthorName(name);
    }
    
    private static void validate(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidAuthorDataException("name", "Author name cannot be empty");
        }
        if (name.length() > 100) {
            throw new InvalidAuthorDataException("name", "Author name cannot exceed 100 characters");
        }
    }
    
    @Override
    public String toString() {
        return value;
    }
}
```

### Biography.java
```java
package com.library.book.domain.model.author;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class Biography {
    private String value;
    
    private Biography(String value) {
        this.value = value;
    }
    
    public static Biography of(String biography) {
        return new Biography(biography);
    }
    
    public static Biography empty() {
        return new Biography(null);
    }
    
    @Override
    public String toString() {
        return value != null ? value : "";
    }
}
```

### Author.java
```java
package com.library.book.domain.model.author;

import com.library.book.domain.event.AuthorCreatedEvent;
import com.library.book.domain.model.shared.AggregateRoot;
import lombok.Getter;

@Getter
public class Author extends AggregateRoot {
    private AuthorId id;
    private AuthorName name;
    private Biography biography;
    private boolean deleted;
    
    // Private constructor for factory method
    private Author() {}
    
    // Factory method
    public static Author create(AuthorName name, Biography biography) {
        Author author = new Author();
        author.id = AuthorId.createNew();
        author.name = name;
        author.biography = biography;
        author.deleted = false;
        
        // Register domain event
        author.registerEvent(new AuthorCreatedEvent(author.id));
        
        return author;
    }
    
    // Business methods
    public void updateName(AuthorName newName) {
        this.name = newName;
    }
    
    public void updateBiography(Biography newBiography) {
        this.biography = newBiography;
    }
    
    public void markAsDeleted() {
        this.deleted = true;
    }
    
    public boolean isDeleted() {
        return deleted;
    }
}
```

### AuthorCreatedEvent.java
```java
package com.library.book.domain.event;

import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.model.shared.DomainEvent;
import lombok.Getter;

@Getter
public class AuthorCreatedEvent extends DomainEvent {
    private final AuthorId authorId;
    
    public AuthorCreatedEvent(AuthorId authorId) {
        super();
        this.authorId = authorId;
    }
}
```

### AuthorRepository.java
```java
package com.library.book.domain.repository;

import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorId;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository {
    Author save(Author author);
    Optional<Author> findById(AuthorId id);
    List<Author> findAll(int page, int size);
    long count();
    void delete(Author author);
}
```

### AuthorDomainService.java
```java
package com.library.book.domain.service;

import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorName;
import com.library.book.domain.model.author.Biography;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorDomainService {
    
    public Author createNewAuthor(String name, String biography) {
        AuthorName authorName = AuthorName.of(name);
        Biography bio = biography != null ? Biography.of(biography) : Biography.empty();
        
        return Author.create(authorName, bio);
    }
}
```

### DomainException.java
```java
package com.library.book.domain.exception;

/**
 * Base exception class for all domain exceptions.
 * Domain exceptions represent business rule violations.
 */
public abstract class DomainException extends RuntimeException {
    
    public DomainException(String message) {
        super(message);
    }
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### AuthorDomainException.java
```java
package com.library.book.domain.exception;

/**
 * Exception thrown when a business rule related to Author is violated.
 */
public class AuthorDomainException extends DomainException {
    
    public AuthorDomainException(String message) {
        super(message);
    }
    
    public AuthorDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### InvalidAuthorDataException.java
```java
package com.library.book.domain.exception;

/**
 * Exception thrown when author data violates domain rules.
 */
public class InvalidAuthorDataException extends AuthorDomainException {
    
    private final String field;
    private final String reason;
    
    public InvalidAuthorDataException(String field, String reason) {
        super(String.format("Invalid author data for field '%s': %s", field, reason));
        this.field = field;
        this.reason = reason;
    }
    
    public String getField() {
        return field;
    }
    
    public String getReason() {
        return reason;
    }
}
```

## Infrastructure Layer

### AuthorJpaEntity.java
```java
package com.library.book.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "authors")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class AuthorJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Lob
    @Column(name = "biography")
    private String biography;
    
    @Column(name = "delete_flg", nullable = false)
    private boolean deleteFlg;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;
    
    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;
}
```

### AuthorJpaRepository.java
```java
package com.library.book.infrastructure.persistence.repository;

import com.library.book.infrastructure.persistence.entity.AuthorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorJpaRepository extends JpaRepository<AuthorJpaEntity, Long> {
    Page<AuthorJpaEntity> findAllByDeleteFlg(boolean deleteFlg, Pageable pageable);
}
```

### AuthorEntityMapper.java
```java
package com.library.book.infrastructure.persistence.mapper;

import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.model.author.AuthorName;
import com.library.book.domain.model.author.Biography;
import com.library.book.infrastructure.persistence.entity.AuthorEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthorEntityMapper {
    
    public AuthorJpaEntity toJpaEntity(Author author) {
        AuthorJpaEntity entity = new AuthorJpaEntity();
        
        if (author.getId() != null && author.getId().getValue() != null) {
            entity.setId(author.getId().getValue());
        }
        
        entity.setName(author.getName().getValue());
        entity.setBiography(author.getBiography().getValue());
        entity.setDeleteFlg(author.isDeleted());
        
        return entity;
    }
    
    public Author toDomainEntity(AuthorJpaEntity jpaEntity) {
        // Sử dụng reflection hoặc constructor riêng để tạo Author từ JPA entity
        // Đây là cách đơn giản hóa, trong thực tế có thể cần constructor package-private trong Author
        
        Author author = Author.create(
            AuthorName.of(jpaEntity.getName()),
            Biography.of(jpaEntity.getBiography())
        );
        
        // Reflection để set ID (trong thực tế nên có setter package-private)
        try {
            java.lang.reflect.Field idField = Author.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(author, new AuthorId(jpaEntity.getId()));
            
            java.lang.reflect.Field deletedField = Author.class.getDeclaredField("deleted");
            deletedField.setAccessible(true);
            deletedField.set(author, jpaEntity.isDeleteFlg());
            
            // Clear events since this is loading from DB
            author.clearEvents();
            
        } catch (Exception e) {
            throw new RuntimeException("Error mapping JPA entity to domain entity", e);
        }
        
        return author;
    }
}
```

### AuthorPersistenceException.java
```java
package com.library.book.infrastructure.exception;

/**
 * Exception thrown when there's an issue with author persistence.
 */
public class AuthorPersistenceException extends RuntimeException {
    
    public AuthorPersistenceException(String message) {
        super(message);
    }
    
    public AuthorPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### AuthorRepositoryImpl.java
```java
package com.library.book.infrastructure.persistence.impl;

import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.repository.AuthorRepository;
import com.library.book.infrastructure.exception.AuthorPersistenceException;
import com.library.book.infrastructure.persistence.entity.AuthorEntity;
import com.library.book.infrastructure.persistence.mapper.AuthorEntityMapper;
import com.library.book.infrastructure.persistence.repository.AuthorJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AuthorRepositoryImpl implements AuthorRepository {
    
    private final AuthorJpaRepository authorJpaRepository;
    private final AuthorEntityMapper authorEntityMapper;
    
    @Override
    public Author save(Author author) {
        try {
            AuthorJpaEntity entity = authorEntityMapper.toJpaEntity(author);
            AuthorJpaEntity savedEntity = authorJpaRepository.save(entity);
            return authorEntityMapper.toDomainEntity(savedEntity);
        } catch (DataAccessException e) {
            log.error("Error saving author", e);
            throw new AuthorPersistenceException("Failed to save author", e);
        } catch (Exception e) {
            log.error("Unexpected error when saving author", e);
            throw new AuthorPersistenceException("Unexpected error when saving author", e);
        }
    }
    
    @Override
    public Optional<Author> findById(AuthorId id) {
        try {
            return authorJpaRepository.findById(id.getValue())
                    .map(authorEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding author by ID: {}", id.getValue(), e);
            throw new AuthorPersistenceException("Failed to find author by ID: " + id.getValue(), e);
        }
    }
    
    @Override
    public List<Author> findAll(int page, int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name"));
            return authorJpaRepository.findAllByDeleteFlg(false, pageRequest)
                    .stream()
                    .map(authorEntityMapper::toDomainEntity)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Error finding all authors", e);
            throw new AuthorPersistenceException("Failed to find all authors", e);
        }
    }
    
    @Override
    public long count() {
        try {
            return authorJpaRepository.count();
        } catch (DataAccessException e) {
            log.error("Error counting authors", e);
            throw new AuthorPersistenceException("Failed to count authors", e);
        }
    }
    
    @Override
    public void delete(Author author) {
        try {
            // Soft delete
            AuthorJpaEntity entity = authorEntityMapper.toJpaEntity(author);
            entity.setDeleteFlg(true);
            authorJpaRepository.save(entity);
        } catch (DataAccessException e) {
            log.error("Error deleting author", e);
            throw new AuthorPersistenceException("Failed to delete author", e);
        }
    }
}
```

### AuthorConfig.java
```java
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
```

## Application Layer

### AuthorApplicationException.java
```java
package com.library.book.application.exception;

/**
 * Base exception for all author-related application exceptions.
 */
public class AuthorApplicationException extends RuntimeException {
    
    public AuthorApplicationException(String message) {
        super(message);
    }
    
    public AuthorApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### AuthorNotFoundException.java
```java
package com.library.book.application.exception;

/**
 * Exception thrown when an author cannot be found.
 */
public class AuthorNotFoundException extends AuthorApplicationException {
    
    private final Object authorId;
    
    public AuthorNotFoundException(Object authorId) {
        super(String.format("Author with ID '%s' not found", authorId));
        this.authorId = authorId;
    }
    
    public Object getAuthorId() {
        return authorId;
    }
}
```

### AuthorCreateRequest.java
```java
package com.library.book.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorCreateRequest {
    @NotBlank(message = "Tên tác giả không được để trống")
    @Size(max = 100, message = "Tên tác giả không được vượt quá 100 ký tự")
    private String name;
    
    private String biography;
}
```

### AuthorResponse.java
```java
package com.library.book.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorResponse {
    private Long id;
    private String name;
    private String biography;
}
```

### AuthorApplicationService.java
```java
package com.library.book.application.service;

import com.library.book.application.dto.request.AuthorCreateRequest;
import com.library.book.application.dto.response.AuthorResponse;
import com.library.book.application.exception.AuthorApplicationException;
import com.library.book.application.exception.AuthorNotFoundException;
import com.library.book.domain.exception.AuthorDomainException;
import com.library.book.domain.exception.InvalidAuthorDataException;
import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.repository.AuthorRepository;
import com.library.book.domain.service.AuthorDomainService;
import com.library.common.aop.annotation.Loggable;
import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorApplicationService {
    
    private final AuthorRepository authorRepository;
    private final AuthorDomainService authorDomainService;
    
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Author",
        logArguments = true,
        logReturnValue = false,
        logExecutionTime = true,
        performanceThresholdMs = 800L,
        messagePrefix = "AUTHOR_APP_SERVICE_LIST"
    )
    public PaginatedResponse<AuthorResponse> getAllAuthors(PaginatedRequest paginatedRequest) {
        List<Author> authors = authorRepository.findAll(
            paginatedRequest.getPage(), 
            paginatedRequest.getSize()
        );
        
        long totalElements = authorRepository.count();
        
        List<AuthorResponse> authorResponses = authors.stream()
            .map(this::mapToAuthorResponse)
            .collect(Collectors.toList());
        
        return new PaginatedResponse<>(
            authorResponses,
            paginatedRequest.getPage(),
            paginatedRequest.getSize(),
            totalElements
        );
    }
    
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "Author",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        performanceThresholdMs = 1500L,
        messagePrefix = "AUTHOR_APP_SERVICE_CREATE"
    )
    public AuthorResponse createAuthor(AuthorCreateRequest request) {
        try {
            Author author = authorDomainService.createNewAuthor(
                request.getName(), 
                request.getBiography()
            );
            
            Author savedAuthor = authorRepository.save(author);
            
            // Xử lý domain events nếu cần
            // eventPublisher.publish(savedAuthor.getDomainEvents());
            
            return mapToAuthorResponse(savedAuthor);
        } catch (InvalidAuthorDataException e) {
            log.error("Invalid author data: {}", e.getMessage());
            throw e; // Rethrow để được xử lý bởi exception handler
        } catch (AuthorDomainException e) {
            log.error("Domain exception when creating author: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error when creating author", e);
            throw new AuthorApplicationException("Failed to create author", e);
        }
    }
    
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Author",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        performanceThresholdMs = 500L,
        messagePrefix = "AUTHOR_APP_SERVICE_GET_BY_ID"
    )
    public AuthorResponse getAuthorById(Long id) {
        return authorRepository.findById(new AuthorId(id))
            .map(this::mapToAuthorResponse)
            .orElseThrow(() -> new AuthorNotFoundException(id));
    }
    
    private AuthorResponse mapToAuthorResponse(Author author) {
        return AuthorResponse.builder()
            .id(author.getId().getValue())
            .name(author.getName().getValue())
            .biography(author.getBiography().getValue())
            .build();
    }
}
```

## Interface Layer

### AuthorController.java
```java
package com.library.book.interfaces.rest;

import com.library.book.application.dto.request.AuthorCreateRequest;
import com.library.book.application.dto.response.AuthorResponse;
import com.library.book.application.service.AuthorApplicationService;
import com.library.common.aop.annotation.Loggable;
import com.library.common.dto.ApiResponse;
import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/authors")
public class AuthorController {

    private final AuthorApplicationService authorApplicationService;

    @GetMapping
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "Author",
        logArguments = true,
        logReturnValue = false,
        performanceThresholdMs = 1000L,
        messagePrefix = "AUTHOR_LIST",
        customTags = {"endpoint=getAllAuthors", "pagination=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<AuthorResponse>>> getAllAuthors(
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(
            authorApplicationService.getAllAuthors(paginatedRequest)
        ));
    }

    @GetMapping("/{authorId}")
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "Author",
        logArguments = true,
        logReturnValue = true,
        performanceThresholdMs = 500L,
        messagePrefix = "AUTHOR_DETAIL",
        customTags = {"endpoint=getAuthorId"}
    )
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthorById(
            @PathVariable("authorId") Long authorId) {
        return ResponseEntity.ok(ApiResponse.success(
            authorApplicationService.getAuthorById(authorId)
        ));
    }

    @PostMapping
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.CREATE,
        resourceType = "Author",
        logArguments = true,
        logReturnValue = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "AUTHOR_CREATION",
        customTags = {"endpoint=createAuthor", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<AuthorResponse>> createAuthor(
            @RequestBody @Valid AuthorCreateRequest authorCreateRequest) {
        return ResponseEntity.ok(ApiResponse.success(
            authorApplicationService.createAuthor(authorCreateRequest)
        ));
    }
}
```

## Exception Handling

### AuthorExceptionHandler.java
```java
package com.library.book.interfaces.rest.exception;

import com.library.book.application.exception.AuthorApplicationException;
import com.library.book.application.exception.AuthorNotFoundException;
import com.library.book.domain.exception.AuthorDomainException;
import com.library.book.domain.exception.InvalidAuthorDataException;
import com.library.book.infrastructure.exception.AuthorPersistenceException;
import com.library.common.dto.ApiError;
import com.library.common.dto.ApiResponse;
import com.library.common.dto.ApiValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

/**
 * Exception handler specifically for Author-related exceptions.
 * Uses a higher order than the global exception handler to ensure it handles author exceptions first.
 */
@RestControllerAdvice(basePackages = "com.library.book.interfaces.rest")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class AuthorExceptionHandler {

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthorNotFound(AuthorNotFoundException ex) {
        log.warn("Author not found: {}", ex.getMessage());
        
        ApiError apiError = new ApiError(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            null
        );
        
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(apiError));
    }
    
    @ExceptionHandler(InvalidAuthorDataException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidAuthorData(InvalidAuthorDataException ex) {
        log.warn("Invalid author data: {}", ex.getMessage());
        
        ApiValidationError validationError = new ApiValidationError(
            ex.getField(),
            ex.getReason()
        );
        
        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid author data",
            Collections.singletonList(validationError)
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(apiError));
    }
    
    @ExceptionHandler(AuthorDomainException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthorDomainException(AuthorDomainException ex) {
        log.warn("Author domain exception: {}", ex.getMessage());
        
        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            null
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(apiError));
    }
    
    @ExceptionHandler(AuthorApplicationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthorApplicationException(AuthorApplicationException ex) {
        log.error("Author application exception", ex);
        
        ApiError apiError = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An error occurred while processing author data",
            null
        );
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(apiError));
    }
    
    @ExceptionHandler(AuthorPersistenceException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthorPersistenceException(AuthorPersistenceException ex) {
        log.error("Author persistence exception", ex);
        
        ApiError apiError = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An error occurred while saving author data",
            null
        );
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(apiError));
    }
}
```

### DddGlobalExceptionHandler.java
```java
package com.library.book.interfaces.rest.exception;

import com.library.book.domain.exception.DomainException;
import com.library.common.dto.ApiError;
import com.library.common.dto.ApiResponse;
import com.library.common.dto.ApiValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for DDD architecture.
 * Handles exceptions that are not caught by more specific handlers.
 */
@RestControllerAdvice
@Slf4j
public class DddGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, 
            WebRequest request) {
        
        List<ApiValidationError> validationErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new ApiValidationError(
                error.getField(), 
                error.getDefaultMessage()
            ))
            .collect(Collectors.toList());

        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            validationErrors
        );

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Object>> handleDomainException(
            DomainException ex, 
            WebRequest request) {
        
        log.warn("Domain exception: {}", ex.getMessage());
        
        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            null
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, 
            WebRequest request) {
        
        log.error("Unhandled exception", ex);
        
        ApiError apiError = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred",
            null
        );
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(apiError));
    }
}
```

## Testing với cURL

Dưới đây là các lệnh cURL để test API Author sau khi migrate sang DDD:

### 1. Lấy danh sách tác giả (phân trang)

```bash
# Lấy danh sách tác giả với tham số phân trang mặc định (page=0, size=10)
curl -X GET "http://localhost:8080/api/v1/authors" -H "Accept: application/json"

# Lấy danh sách tác giả với tham số phân trang tùy chỉnh
curl -X GET "http://localhost:8080/api/v1/authors?page=0&size=5" -H "Accept: application/json"

# Lấy danh sách tác giả với sắp xếp
curl -X GET "http://localhost:8080/api/v1/authors?page=0&size=10&sortBy=name&sortDirection=asc" -H "Accept: application/json"
```

### 2. Lấy thông tin tác giả theo ID

```bash
# Lấy thông tin tác giả có ID = 1
curl -X GET "http://localhost:8080/api/v1/authors/1" -H "Accept: application/json"
```

### 3. Tạo tác giả mới

```bash
# Tạo tác giả mới với thông tin hợp lệ
curl -X POST "http://localhost:8080/api/v1/authors" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "Nguyễn Nhật Ánh",
    "biography": "Nhà văn nổi tiếng với nhiều tác phẩm văn học thiếu nhi và thanh niên"
  }'

# Tạo tác giả mới với tên quá dài (lỗi validation)
curl -X POST "http://localhost:8080/api/v1/authors" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "Tên tác giả quá dài để test validation - Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
    "biography": "Test biography"
  }'

# Tạo tác giả mới với tên trống (lỗi validation)
curl -X POST "http://localhost:8080/api/v1/authors" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "",
    "biography": "Test biography"
  }'
```

### 4. Kiểm tra xử lý lỗi

```bash
# Kiểm tra xử lý lỗi khi tác giả không tồn tại
curl -X GET "http://localhost:8080/api/v1/authors/999" -H "Accept: application/json"
```

## Kết luận

Việc migrate module Author sang DDD đã mang lại nhiều lợi ích:

1. **Domain model phong phú hơn**:
    - Sử dụng Value Objects (AuthorName, Biography) để bảo vệ invariants
    - Sử dụng Factory Method để tạo Author
    - Sử dụng Domain Events để thông báo các sự kiện domain

2. **Tách biệt rõ ràng các concerns**:
    - Domain Layer: Chứa business logic
    - Application Layer: Điều phối use cases
    - Infrastructure Layer: Xử lý persistence
    - Interface Layer: Xử lý giao tiếp với client

3. **Exception handling theo layers**:
    - Domain exceptions: Xử lý vi phạm business rules
    - Application exceptions: Xử lý lỗi use case
    - Infrastructure exceptions: Xử lý lỗi kỹ thuật

4. **Testability cao hơn**:
    - Dễ dàng mock dependencies
    - Dễ dàng test domain logic độc lập

Với kiến trúc DDD này, module Author đã trở nên rõ ràng hơn về mặt domain, dễ bảo trì và mở rộng hơn, đồng thời giúp team hiểu sâu hơn về business logic của hệ thống.
# Audit System Implementation Guide

This guide explains how to implement and extend the audit event system for tracking entity changes in the catalog-service.

## Overview

The audit system publishes events to Kafka whenever entities are created, updated, or deleted. These events are consumed by the dashboard-service for audit logging and monitoring.

## Architecture Components

### 1. Event Structure
- **AuditEventMessage**: Standard event format containing entity information, operation type, and user details
- **EventType**: Enum defining operation types (CREATED, UPDATED, DELETED)
- **AuditEventBuilder**: Builder pattern for creating audit events

### 2. Publishing Infrastructure
- **AuditEventPublisher**: Interface for publishing events to Kafka
- **AuditEventPublisherImpl**: Kafka implementation of the publisher
- **AuditService**: High-level service for publishing audit events

### 3. Configuration
- **KafkaConfig**: Kafka producer configuration
- **application.properties**: Topic and connection settings

## Current Implementation

The Author entity is already configured with audit events for:
- ✅ Create operations
- ✅ Update operations  
- ✅ Delete operations

## Adding Audit Events to New Entities

### Step 1: Update Business Service

For any new entity (e.g., Book, Category), inject the `AuditService` and call the appropriate methods:

```java
@Service
@RequiredArgsConstructor
public class BookBusinessImpl implements BookBusiness {
    
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final AuditService auditService; // Add this dependency
    
    @Transactional
    public BookResponse createBook(CreateBookRequest request, String currentUser) {
        // Business logic...
        Book book = bookMapper.toEntity(request);
        bookRepository.save(book);
        
        // Publish audit event
        auditService.publishCreateEvent("Book", book.getId().toString(), book, currentUser);
        
        return bookMapper.toResponse(book);
    }
    
    @Transactional
    public BookResponse updateBook(Integer id, UpdateBookRequest request, String currentUser) {
        Book existingBook = bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException(id));
            
        // Store old values for audit
        Book oldBook = createCopyForAudit(existingBook);
        
        // Update logic...
        bookMapper.updateEntity(existingBook, request);
        bookRepository.save(existingBook);
        
        // Publish audit event
        auditService.publishUpdateEvent("Book", existingBook.getId().toString(), 
                                       oldBook, existingBook, currentUser);
        
        return bookMapper.toResponse(existingBook);
    }
    
    @Transactional
    public void deleteBook(Integer id, String currentUser) {
        Book existingBook = bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException(id));
            
        // Store old values for audit
        Book oldBook = createCopyForAudit(existingBook);
        
        // Soft delete logic...
        existingBook.setDeleteFlag(true);
        bookRepository.save(existingBook);
        
        // Publish audit event
        auditService.publishDeleteEvent("Book", existingBook.getId().toString(), 
                                       oldBook, currentUser);
    }
}
```

### Step 2: Entity Type Naming Convention

Use consistent entity type names:
- `"Author"` for Author entities
- `"Book"` for Book entities  
- `"Category"` for Category entities
- etc.

### Step 3: Handle Old Values for Updates/Deletes

Create a copy of the entity before modifications to capture old values:

```java
private Book createCopyForAudit(Book original) {
    Book copy = new Book();
    copy.setId(original.getId());
    copy.setTitle(original.getTitle());
    copy.setIsbn(original.getIsbn());
    // Copy other relevant fields...
    return copy;
}
```

## Configuration

### Catalog Service (Producer)

Add to `application.properties`:
```properties
# Audit Configuration
audit.kafka.topic=catalog-service-audit-logs
```

### Dashboard Service (Consumer)

Add to `application.properties`:
```properties
# Audit Configuration  
audit.kafka.topics=catalog-service-audit-logs,other-service-audit-logs
```

## Event Flow

1. **Entity Operation**: User performs create/update/delete on entity
2. **Business Logic**: Service executes business logic and saves to database
3. **Audit Event**: Service publishes audit event to Kafka topic
4. **Event Consumption**: Dashboard service consumes event from Kafka
5. **Audit Logging**: Dashboard service stores audit log in database

## Best Practices

### 1. Error Handling
- Audit events should not break main business flow
- Use try-catch blocks around audit publishing
- Log audit failures but don't throw exceptions

### 2. Performance
- Audit publishing is asynchronous via Kafka
- Minimal impact on main business operations
- Use appropriate batch sizes and compression

### 3. Data Privacy
- Don't include sensitive data in audit events
- Consider data masking for PII fields
- Follow data retention policies

### 4. Testing
- Test audit events in integration tests
- Verify event structure and content
- Test error scenarios

## Monitoring

Monitor the audit system through:
- Kafka topic metrics (lag, throughput)
- Dashboard service logs
- Audit log database queries
- Application metrics and alerts

## Troubleshooting

### Common Issues

1. **Events not published**: Check Kafka connectivity and topic configuration
2. **Events not consumed**: Verify consumer group and topic subscription
3. **Serialization errors**: Ensure ObjectMapper configuration is consistent
4. **Performance issues**: Review batch sizes and compression settings

### Debugging

Enable debug logging:
```properties
logging.level.com.library.catalog.business.kafka=DEBUG
logging.level.com.library.catalog.business.service=DEBUG
```

## Future Enhancements

1. **Aspect-Oriented Programming**: Use `@AuditableEntity` annotation for automatic event publishing
2. **Event Filtering**: Add configuration for selective event publishing
3. **Event Enrichment**: Add more context information to events
4. **Dead Letter Queue**: Handle failed event processing
5. **Event Replay**: Support for replaying audit events
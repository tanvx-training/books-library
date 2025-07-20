# Reusable Exception System Guide

This guide explains how to use the generic, reusable exception system in the catalog-service that can be applied to any entity type (Author, Book, Category, etc.).

## Overview

The reusable exception system replaces entity-specific exceptions (like `AuthorNotFoundException`, `AuthorValidationException`) with generic exceptions that can be used for any entity type. This approach reduces code duplication and provides consistent error handling across all entities.

## Exception Classes

### 1. EntityNotFoundException
Used when an entity cannot be found by ID or other criteria.

```java
// Basic usage
throw EntityNotFoundException.forEntity("Book", bookId);

// With custom criteria
throw EntityNotFoundException.forCriteria("Author", "name='John Doe'");

// With custom message
throw new EntityNotFoundException("Book", bookId, "Book is not available for borrowing");
```

### 2. EntityValidationException
Used for business rule validation failures.

```java
// Duplicate value validation
throw EntityValidationException.duplicateValue("Author", "name", authorName);

// Invalid field validation
throw EntityValidationException.invalidField("Book", "isbn", isbn, "must be 13 digits");

// Required field validation
throw EntityValidationException.requiredField("Category", "name");

// Business rule validation
throw EntityValidationException.businessRule("Book", "cannot delete book with active loans");
```

### 3. EntityServiceException
Used for service-level errors (database, external services, etc.).

```java
// Database errors
throw EntityServiceException.databaseError("Author", "create", cause);

// Mapping errors
throw EntityServiceException.mappingError("Book", cause);

// External service errors
throw EntityServiceException.externalServiceError("Book", "create", "ISBN-Service", cause);

// Concurrency conflicts
throw EntityServiceException.concurrencyConflict("Author", "update", authorId);
```

## Utility Class: EntityExceptionUtils

The `EntityExceptionUtils` class provides helper methods to reduce boilerplate code:

### Common Validation Methods

```java
// Validate non-null values
EntityExceptionUtils.requireNonNull(bookId, "Book", "id");

// Validate non-empty strings
String title = EntityExceptionUtils.requireNonEmpty(request.getTitle(), "Book", "title");

// Validate Optional results
Book book = EntityExceptionUtils.requireFound(
    bookRepository.findById(id), 
    "Book", 
    id
);

// Check for duplicates
EntityExceptionUtils.requireNoDuplicate(
    bookRepository.existsByIsbn(isbn),
    "Book", "isbn", isbn
);

// Validate conditions
EntityExceptionUtils.requireValid(
    book.getStatus() == BookStatus.AVAILABLE,
    "Book", "Book must be available for borrowing"
);
```

## Implementation Examples

### Example 1: Book Service Implementation

```java
@Service
@RequiredArgsConstructor
public class BookBusinessImpl implements BookBusiness {
    
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final AuditService auditService;
    
    @Transactional
    public BookResponse createBook(CreateBookRequest request, String currentUser) {
        
        // Validate required fields
        EntityExceptionUtils.requireNonEmpty(request.getTitle(), "Book", "title");
        EntityExceptionUtils.requireNonEmpty(request.getIsbn(), "Book", "isbn");
        
        // Check for duplicate ISBN
        EntityExceptionUtils.requireNoDuplicate(
            bookRepository.existsByIsbn(request.getIsbn()),
            "Book", "isbn", request.getIsbn()
        );
        
        // Business logic
        Book book = bookMapper.toEntity(request);
        book.setCreatedBy(currentUser);
        bookRepository.save(book);
        
        // Publish audit event
        auditService.publishCreateEvent("Book", book.getId().toString(), book, currentUser);
        
        return bookMapper.toResponse(book);
    }
    
    @Transactional(readOnly = true)
    public BookResponse getBookById(Integer id) {
        
        EntityExceptionUtils.requireNonNull(id, "Book", "id");
        
        Book book = EntityExceptionUtils.requireFound(
            bookRepository.findByIdAndDeleteFlagFalse(id),
            "Book", 
            id
        );
        
        return bookMapper.toResponse(book);
    }
    
    @Transactional
    public BookResponse updateBook(Integer id, UpdateBookRequest request, String currentUser) {
        
        EntityExceptionUtils.requireNonNull(id, "Book", "id");
        EntityExceptionUtils.requireNonEmpty(request.getTitle(), "Book", "title");
        
        Book existingBook = EntityExceptionUtils.requireFound(
            bookRepository.findByIdAndDeleteFlagFalse(id),
            "Book", 
            id
        );
        
        // Store old values for audit
        Book oldBook = createBookCopy(existingBook);
        
        // Check for duplicate ISBN (excluding current book)
        bookRepository.findByIsbnAndDeleteFlagFalse(request.getIsbn())
            .ifPresent(book -> {
                if (!book.getId().equals(id)) {
                    throw EntityValidationException.duplicateValue("Book", "isbn", request.getIsbn());
                }
            });
        
        // Update and save
        bookMapper.updateEntity(existingBook, request);
        existingBook.setUpdatedBy(currentUser);
        bookRepository.save(existingBook);
        
        // Publish audit event
        auditService.publishUpdateEvent("Book", existingBook.getId().toString(), oldBook, existingBook, currentUser);
        
        return bookMapper.toResponse(existingBook);
    }
    
    @Transactional
    public void deleteBook(Integer id, String currentUser) {
        
        EntityExceptionUtils.requireNonNull(id, "Book", "id");
        
        Book existingBook = EntityExceptionUtils.requireFound(
            bookRepository.findByIdAndDeleteFlagFalse(id),
            "Book", 
            id
        );
        
        // Business rule validation
        EntityExceptionUtils.requireValid(
            !hasActiveLoans(existingBook),
            "Book", "Cannot delete book with active loans"
        );
        
        // Store old values for audit
        Book oldBook = createBookCopy(existingBook);
        
        // Soft delete
        existingBook.setDeleteFlag(true);
        existingBook.setUpdatedBy(currentUser);
        bookRepository.save(existingBook);
        
        // Publish audit event
        auditService.publishDeleteEvent("Book", existingBook.getId().toString(), oldBook, currentUser);
    }
    
    private Book createBookCopy(Book original) {
        Book copy = new Book();
        copy.setId(original.getId());
        copy.setTitle(original.getTitle());
        copy.setIsbn(original.getIsbn());
        copy.setDeleteFlag(original.getDeleteFlag());
        return copy;
    }
    
    private boolean hasActiveLoans(Book book) {
        // Check if book has active loans
        return false; // Implementation depends on loan service
    }
}
```

### Example 2: Category Service Implementation

```java
@Service
@RequiredArgsConstructor
public class CategoryBusinessImpl implements CategoryBusiness {
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AuditService auditService;
    
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request, String currentUser) {
        
        // Validate required fields
        EntityExceptionUtils.requireNonEmpty(request.getName(), "Category", "name");
        
        // Check for duplicate name
        EntityExceptionUtils.requireNoDuplicate(
            categoryRepository.existsByNameIgnoreCaseAndDeleteFlagFalse(request.getName()),
            "Category", "name", request.getName()
        );
        
        // Business logic
        Category category = categoryMapper.toEntity(request);
        category.setCreatedBy(currentUser);
        categoryRepository.save(category);
        
        // Publish audit event
        auditService.publishCreateEvent("Category", category.getId().toString(), category, currentUser);
        
        return categoryMapper.toResponse(category);
    }
    
    // Similar pattern for other CRUD operations...
}
```

## Exception Handler Configuration

The `GlobalExceptionHandler` automatically handles all generic exceptions:

```java
@ExceptionHandler(EntityNotFoundException.class)
public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
    String errorCode = ex.getEntityType() != null ? 
        ex.getEntityType().toUpperCase() + "_NOT_FOUND" : "ENTITY_NOT_FOUND";
    
    ErrorResponse errorResponse = new ErrorResponse(
        ex.getMessage(),
        errorCode,
        getPath(request)
    );
    
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
}
```

This automatically generates appropriate error codes:
- `AUTHOR_NOT_FOUND` for Author entities
- `BOOK_NOT_FOUND` for Book entities
- `CATEGORY_NOT_FOUND` for Category entities

## Migration from Entity-Specific Exceptions

### Before (Entity-Specific)
```java
// Old approach - separate exception for each entity
throw AuthorNotFoundException.forId(authorId);
throw BookNotFoundException.forId(bookId);
throw CategoryNotFoundException.forId(categoryId);

// Separate exception handlers
@ExceptionHandler(AuthorNotFoundException.class)
@ExceptionHandler(BookNotFoundException.class)
@ExceptionHandler(CategoryNotFoundException.class)
```

### After (Generic)
```java
// New approach - single exception for all entities
throw EntityNotFoundException.forEntity("Author", authorId);
throw EntityNotFoundException.forEntity("Book", bookId);
throw EntityNotFoundException.forEntity("Category", categoryId);

// Single exception handler for all entities
@ExceptionHandler(EntityNotFoundException.class)
```

## Best Practices

### 1. Consistent Entity Type Naming
Use consistent entity type names across your application:
- `"Author"` for Author entities
- `"Book"` for Book entities
- `"Category"` for Category entities
- Use PascalCase for entity type names

### 2. Use Utility Methods
Prefer `EntityExceptionUtils` methods over direct exception throwing:
```java
// Good
EntityExceptionUtils.requireFound(optional, "Book", id);

// Less preferred
optional.orElseThrow(() -> EntityNotFoundException.forEntity("Book", id));
```

### 3. Meaningful Error Messages
Provide context-specific error messages:
```java
// Good
EntityValidationException.businessRule("Book", "Cannot delete book with active loans");

// Less informative
EntityValidationException.businessRule("Book", "Validation failed");
```

### 4. Proper Field Names
Use descriptive field names in validation exceptions:
```java
// Good
EntityExceptionUtils.requireNonEmpty(searchTerm, "Book", "search term");

// Less clear
EntityExceptionUtils.requireNonEmpty(searchTerm, "Book", "input");
```

## Testing

Test the exceptions using the generic approach:

```java
@Test
void createBook_WithDuplicateIsbn_ShouldThrowEntityValidationException() {
    // Given
    when(bookRepository.existsByIsbn("123456789")).thenReturn(true);
    
    // When & Then
    EntityValidationException exception = assertThrows(
        EntityValidationException.class,
        () -> bookService.createBook(request, "user123")
    );
    
    assertEquals("Book", exception.getEntityType());
    assertEquals("isbn", exception.getField());
    assertTrue(exception.getMessage().contains("already exists"));
}
```

## Benefits

1. **Reduced Code Duplication**: Single set of exceptions for all entities
2. **Consistent Error Handling**: Uniform error responses across all entities
3. **Easier Maintenance**: Changes to exception handling affect all entities
4. **Better Extensibility**: Adding new entities requires no new exception classes
5. **Cleaner Code**: Utility methods reduce boilerplate code
6. **Type Safety**: Generic exceptions still provide type information

## Migration Checklist

When adding a new entity or migrating existing ones:

- [ ] Use `EntityNotFoundException` instead of entity-specific not found exceptions
- [ ] Use `EntityValidationException` instead of entity-specific validation exceptions
- [ ] Use `EntityServiceException` instead of entity-specific service exceptions
- [ ] Import and use `EntityExceptionUtils` for common validations
- [ ] Use consistent entity type naming (PascalCase)
- [ ] Update tests to expect generic exceptions
- [ ] Remove old entity-specific exception classes (after migration)
- [ ] Update documentation and examples

This reusable exception system provides a solid foundation for consistent error handling across all entities in your catalog service!
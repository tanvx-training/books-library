# Book Service Database Schema Documentation

## Overview

This document describes the PostgreSQL database schema for the book-service microservice in the library management system. The database follows Domain-Driven Design (DDD) principles and supports the complete book management lifecycle.

## Database Structure

### Core Tables

#### 1. **authors**
Stores information about book authors.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier |
| name | VARCHAR(255) | NOT NULL | Author's full name |
| biography | TEXT | | Author's biographical information |
| delete_flg | BOOLEAN | NOT NULL, DEFAULT FALSE | Soft delete flag |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Last update timestamp |
| created_by | VARCHAR(36) | | Keycloak ID of creator |
| updated_by | VARCHAR(36) | | Keycloak ID of last updater |

**Indexes:**
- `idx_authors_name` - For name searches
- `idx_authors_delete_flg` - For filtering active authors
- `idx_authors_created_at` - For chronological queries

#### 2. **publishers**
Stores information about book publishers.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier |
| name | VARCHAR(256) | NOT NULL | Publisher name |
| address | TEXT | | Publisher's address |
| delete_flg | BOOLEAN | NOT NULL, DEFAULT FALSE | Soft delete flag |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Last update timestamp |
| created_by | VARCHAR(36) | | Keycloak ID of creator |
| updated_by | VARCHAR(36) | | Keycloak ID of last updater |

**Indexes:**
- `idx_publishers_name` - For name searches
- `idx_publishers_delete_flg` - For filtering active publishers

#### 3. **categories**
Stores book categorization information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier |
| name | VARCHAR(256) | NOT NULL | Category name |
| slug | VARCHAR(256) | NOT NULL, UNIQUE | URL-friendly identifier |
| description | TEXT | | Category description |
| delete_flg | BOOLEAN | NOT NULL, DEFAULT FALSE | Soft delete flag |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Last update timestamp |
| created_by | VARCHAR(36) | | Keycloak ID of creator |
| updated_by | VARCHAR(36) | | Keycloak ID of last updater |

**Indexes:**
- `idx_categories_name` - For name searches
- `idx_categories_slug` - For slug lookups
- `idx_categories_delete_flg` - For filtering active categories

#### 4. **books**
Stores book information and metadata.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier |
| title | VARCHAR(200) | NOT NULL | Book title |
| isbn | VARCHAR(20) | UNIQUE | International Standard Book Number |
| publisher_id | BIGINT | FK to publishers | Publisher reference |
| publication_year | INTEGER | | Year of publication |
| description | TEXT | | Book description |
| cover_image_url | VARCHAR(500) | | URL to cover image |
| delete_flg | BOOLEAN | NOT NULL, DEFAULT FALSE | Soft delete flag |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Last update timestamp |
| created_by | VARCHAR(36) | | Keycloak ID of creator |
| updated_by | VARCHAR(36) | | Keycloak ID of last updater |

**Foreign Keys:**
- `fk_books_publisher` - References publishers(id)

**Indexes:**
- `idx_books_title` - For title searches
- `idx_books_isbn` - For ISBN lookups
- `idx_books_publisher_id` - For publisher queries
- `idx_books_publication_year` - For year-based queries
- `idx_books_title_gin` - Full-text search on title
- `idx_books_description_gin` - Full-text search on description

#### 5. **book_authors**
Many-to-many relationship between books and authors.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| book_id | BIGINT | NOT NULL, FK to books | Book reference |
| author_id | BIGINT | NOT NULL, FK to authors | Author reference |

**Primary Key:** (book_id, author_id)

**Foreign Keys:**
- `fk_book_authors_book` - References books(id) ON DELETE CASCADE
- `fk_book_authors_author` - References authors(id) ON DELETE CASCADE

#### 6. **book_categories**
Many-to-many relationship between books and categories.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| book_id | BIGINT | NOT NULL, FK to books | Book reference |
| category_id | BIGINT | NOT NULL, FK to categories | Category reference |

**Primary Key:** (book_id, category_id)

**Foreign Keys:**
- `fk_book_categories_book` - References books(id) ON DELETE CASCADE
- `fk_book_categories_category` - References categories(id) ON DELETE CASCADE

#### 7. **book_copies**
Physical copies of books available for borrowing.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier |
| book_id | BIGINT | NOT NULL, FK to books | Book reference |
| copy_number | VARCHAR(20) | NOT NULL | Copy identifier within book |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'AVAILABLE' | Copy status |
| condition | VARCHAR(20) | DEFAULT 'GOOD' | Physical condition |
| location | VARCHAR(50) | | Physical location in library |
| acquired_date | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Date acquired |
| current_borrower_keycloak_id | VARCHAR(36) | | Current borrower's Keycloak ID |
| borrowed_date | TIMESTAMP | | Date borrowed |
| due_date | TIMESTAMP | | Due date for return |
| delete_flg | BOOLEAN | NOT NULL, DEFAULT FALSE | Soft delete flag |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Last update timestamp |
| created_by | VARCHAR(36) | | Keycloak ID of creator |
| updated_by | VARCHAR(36) | | Keycloak ID of last updater |

**Constraints:**
- `uk_book_copies_book_copy` - UNIQUE(book_id, copy_number)
- `chk_book_copies_status` - Status must be valid enum value
- `chk_book_copies_condition` - Condition must be valid enum value
- `chk_book_copies_borrower_dates` - Business logic for borrowed status

**Enum Values:**
- **Status**: AVAILABLE, BORROWED, RESERVED, MAINTENANCE, LOST, DAMAGED
- **Condition**: NEW, EXCELLENT, GOOD, FAIR, POOR

**Indexes:**
- `idx_book_copies_book_id` - For book-specific queries
- `idx_book_copies_status` - For status-based queries
- `idx_book_copies_borrower` - For borrower-specific queries
- `idx_book_copies_due_date` - For overdue detection
- `idx_book_copies_book_status` - Composite index for common queries

## Views

### 1. **v_books_complete**
Comprehensive view of books with publisher information and copy statistics.

```sql
SELECT 
    b.id, b.title, b.isbn, b.publication_year, b.description,
    p.name as publisher_name,
    COUNT(bc.id) as total_copies,
    COUNT(CASE WHEN bc.status = 'AVAILABLE' THEN 1 END) as available_copies
FROM books b
LEFT JOIN publishers p ON b.publisher_id = p.id
LEFT JOIN book_copies bc ON b.id = bc.book_id
WHERE b.delete_flg = FALSE
GROUP BY b.id, p.id;
```

### 2. **v_overdue_book_copies**
View of overdue book copies with borrower information.

```sql
SELECT 
    bc.id, bc.book_id, bc.current_borrower_keycloak_id,
    bc.due_date, CURRENT_DATE - bc.due_date::date as days_overdue,
    b.title as book_title
FROM book_copies bc
JOIN books b ON bc.book_id = b.id
WHERE bc.status = 'BORROWED' 
  AND bc.due_date < CURRENT_TIMESTAMP;
```

## Functions

### Business Logic Functions

#### 1. **can_book_be_deleted(book_id)**
Checks if a book can be safely deleted (no active borrowings/reservations).

```sql
SELECT can_book_be_deleted(1); -- Returns boolean
```

#### 2. **can_author_be_deleted(author_id)**
Checks if an author can be deleted (no associated books).

#### 3. **can_publisher_be_deleted(publisher_id)**
Checks if a publisher can be deleted (no associated books).

#### 4. **can_category_be_deleted(category_id)**
Checks if a category can be deleted (no associated books).

## Triggers

### Automatic Timestamp Updates
All tables have triggers that automatically update the `updated_at` column when records are modified.

```sql
CREATE TRIGGER update_books_updated_at 
BEFORE UPDATE ON books 
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

## Indexes and Performance

### Full-Text Search
- Books table has GIN indexes for full-text search on title and description
- Supports advanced search functionality across book content

### Query Optimization
- Composite indexes on frequently queried column combinations
- Separate indexes for filtering (delete_flg, status) and sorting (created_at, due_date)

## Data Integrity

### Foreign Key Constraints
- All relationships properly enforced with foreign keys
- CASCADE deletes for junction tables (book_authors, book_categories)
- RESTRICT deletes for core entities to prevent data loss

### Check Constraints
- Enum values enforced at database level
- Business logic constraints for borrowing workflow

### Unique Constraints
- ISBN uniqueness across books
- Category slug uniqueness
- Copy number uniqueness within each book

## Security Considerations

### Audit Trail
- All tables include created_by/updated_by fields for audit tracking
- Timestamps for all create/update operations
- Soft deletes preserve historical data

### Data Privacy
- Borrower information stored as Keycloak IDs (not personal data)
- No sensitive personal information in book service database

## Sample Queries

### Common Operations

#### Find Available Copies of a Book
```sql
SELECT bc.* FROM book_copies bc
JOIN books b ON bc.book_id = b.id
WHERE b.isbn = '978-0-7432-7356-5'
  AND bc.status = 'AVAILABLE'
  AND bc.delete_flg = FALSE;
```

#### Search Books by Title or Author
```sql
SELECT DISTINCT b.* FROM books b
LEFT JOIN book_authors ba ON b.id = ba.book_id
LEFT JOIN authors a ON ba.author_id = a.id
WHERE (b.title ILIKE '%gatsby%' OR a.name ILIKE '%fitzgerald%')
  AND b.delete_flg = FALSE;
```

#### Get User's Borrowed Books
```sql
SELECT b.title, bc.copy_number, bc.due_date
FROM book_copies bc
JOIN books b ON bc.book_id = b.id
WHERE bc.current_borrower_keycloak_id = 'user-123-456-789'
  AND bc.status = 'BORROWED';
```

#### Find Overdue Books
```sql
SELECT * FROM v_overdue_book_copies
ORDER BY days_overdue DESC;
```

## Migration Strategy

### Flyway Integration
- Database migrations managed through Flyway
- Version-controlled schema changes
- Rollback capabilities for schema updates

### Migration Files
- `V1__Create_book_service_tables.sql` - Initial schema
- `V2__Insert_sample_data.sql` - Sample data for testing
- Future migrations follow semantic versioning

## Backup and Recovery

### Recommended Backup Strategy
1. **Daily full backups** of the entire database
2. **Continuous WAL archiving** for point-in-time recovery
3. **Weekly backup verification** and restore testing
4. **Cross-region backup replication** for disaster recovery

### Critical Data
- Books and metadata (core business data)
- Book copies and borrowing history (operational data)
- Author and publisher information (reference data)

## Performance Monitoring

### Key Metrics to Monitor
- Query execution times for book searches
- Index usage statistics
- Connection pool utilization
- Slow query identification

### Optimization Recommendations
- Regular VACUUM and ANALYZE operations
- Monitor and optimize full-text search queries
- Consider partitioning for large book_copies table
- Implement connection pooling for high-traffic scenarios

## Conclusion

This database schema provides a robust foundation for the book service, supporting:
- Complete book lifecycle management
- Efficient search and discovery
- Borrowing and reservation workflows
- Audit trails and data integrity
- Scalable performance characteristics

The schema follows PostgreSQL best practices and DDD principles, ensuring maintainability and extensibility for future requirements.
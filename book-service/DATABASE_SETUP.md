# Book Service Database Setup Guide

## Overview

This guide provides instructions for setting up the PostgreSQL database for the book-service microservice.

## Quick Start with Docker Compose

### 1. Start Database Services

```bash
# Start PostgreSQL and pgAdmin
docker-compose -f docker-compose-db.yml up -d

# Check if services are running
docker-compose -f docker-compose-db.yml ps
```

### 2. Verify Database Setup

```bash
# Connect to the database
docker exec -it book-service-postgres psql -U book_service_user -d book_service

# Run a test query
SELECT COUNT(*) FROM books;
```

### 3. Access pgAdmin (Optional)

- URL: http://localhost:5050
- Email: admin@bookservice.local
- Password: admin123

## Manual Database Setup

### Prerequisites

- PostgreSQL 12+ installed
- Access to PostgreSQL superuser account

### 1. Create Database and User

```bash
# Connect as superuser
psql -U postgres

# Run setup script
\i book-service/scripts/setup-database.sql
```

### 2. Run Migrations

```bash
# Connect to book_service database
psql -U book_service_user -d book_service

# Create schema
\i book-service/src/main/resources/db/migration/V1__Create_book_service_tables.sql

# Insert sample data
\i book-service/src/main/resources/db/migration/V2__Insert_sample_data.sql
```

## Database Configuration

### Connection Details

| Parameter | Value |
|-----------|-------|
| Host | localhost |
| Port | 5433 (Docker) / 5432 (Manual) |
| Database | book_service |
| Username | book_service_user |
| Password | book_service_password_2024 |

### Spring Boot Configuration

Update your `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/book_service
    username: book_service_user
    password: book_service_password_2024
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: none  # Use Flyway for migrations
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
```

## Database Schema

### Core Tables

1. **authors** - Book authors information
2. **publishers** - Publisher information
3. **categories** - Book categories
4. **books** - Book metadata
5. **book_authors** - Many-to-many: books ↔ authors
6. **book_categories** - Many-to-many: books ↔ categories
7. **book_copies** - Physical book copies for borrowing

### Key Features

- **Soft Deletes**: All entities use `delete_flg` for soft deletion
- **Audit Trail**: Created/updated timestamps and user tracking
- **Full-Text Search**: GIN indexes for book title and description search
- **Business Logic**: Database functions for validation rules
- **Performance**: Optimized indexes for common queries

## Sample Data

The database includes sample data:
- 10 authors (F. Scott Fitzgerald, Harper Lee, etc.)
- 10 publishers (Penguin Random House, HarperCollins, etc.)
- 15 categories (Fiction, Science Fiction, Mystery, etc.)
- 10 books (The Great Gatsby, 1984, Harry Potter, etc.)
- 25 book copies with various statuses

## Common Operations

### Check Database Status

```sql
-- Verify tables exist
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' ORDER BY table_name;

-- Check sample data
SELECT 
    (SELECT COUNT(*) FROM authors) as authors,
    (SELECT COUNT(*) FROM publishers) as publishers,
    (SELECT COUNT(*) FROM categories) as categories,
    (SELECT COUNT(*) FROM books) as books,
    (SELECT COUNT(*) FROM book_copies) as book_copies;
```

### Search Books

```sql
-- Search by title
SELECT * FROM books WHERE title ILIKE '%gatsby%';

-- Full-text search
SELECT * FROM books 
WHERE to_tsvector('english', title || ' ' || COALESCE(description, '')) 
      @@ plainto_tsquery('english', 'american novel');
```

### Check Available Copies

```sql
-- Available copies by book
SELECT b.title, COUNT(bc.id) as available_copies
FROM books b
JOIN book_copies bc ON b.id = bc.book_id
WHERE bc.status = 'AVAILABLE' AND bc.delete_flg = FALSE
GROUP BY b.id, b.title;
```

### Find Overdue Books

```sql
-- Using the overdue view
SELECT * FROM v_overdue_book_copies;
```

## Troubleshooting

### Common Issues

#### 1. Connection Refused
```bash
# Check if PostgreSQL is running
docker-compose -f docker-compose-db.yml ps

# Check logs
docker-compose -f docker-compose-db.yml logs book-service-db
```

#### 2. Authentication Failed
```bash
# Verify user exists
docker exec -it book-service-postgres psql -U postgres -c "SELECT usename FROM pg_user WHERE usename = 'book_service_user';"
```

#### 3. Database Not Found
```bash
# Verify database exists
docker exec -it book-service-postgres psql -U postgres -c "SELECT datname FROM pg_database WHERE datname = 'book_service';"
```

#### 4. Tables Not Found
```bash
# Check if migrations ran
docker exec -it book-service-postgres psql -U book_service_user -d book_service -c "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';"
```

### Reset Database

```bash
# Stop services
docker-compose -f docker-compose-db.yml down

# Remove volumes (WARNING: This deletes all data)
docker volume rm book-service_book_service_postgres_data

# Start fresh
docker-compose -f docker-compose-db.yml up -d
```

## Performance Tuning

### Recommended PostgreSQL Settings

For development:
```
shared_buffers = 256MB
effective_cache_size = 1GB
maintenance_work_mem = 64MB
checkpoint_completion_target = 0.9
wal_buffers = 16MB
default_statistics_target = 100
```

For production, adjust based on available memory and workload.

### Monitoring Queries

```sql
-- Check index usage
SELECT schemaname, tablename, attname, n_distinct, correlation
FROM pg_stats
WHERE schemaname = 'public' AND tablename IN ('books', 'book_copies');

-- Check slow queries (requires pg_stat_statements extension)
SELECT query, calls, total_time, mean_time
FROM pg_stat_statements
ORDER BY mean_time DESC
LIMIT 10;
```

## Backup and Recovery

### Backup

```bash
# Full database backup
docker exec book-service-postgres pg_dump -U book_service_user book_service > book_service_backup.sql

# Schema only
docker exec book-service-postgres pg_dump -U book_service_user -s book_service > book_service_schema.sql

# Data only
docker exec book-service-postgres pg_dump -U book_service_user -a book_service > book_service_data.sql
```

### Restore

```bash
# Restore full backup
docker exec -i book-service-postgres psql -U book_service_user book_service < book_service_backup.sql
```

## Migration with Flyway

The application uses Flyway for database migrations:

1. **V1__Create_book_service_tables.sql** - Initial schema
2. **V2__Insert_sample_data.sql** - Sample data

Flyway will automatically run these migrations when the application starts.

## Security Considerations

- Change default passwords in production
- Use environment variables for sensitive configuration
- Enable SSL connections in production
- Regularly update PostgreSQL version
- Monitor and audit database access

## Support

For issues related to:
- **Database Schema**: Check the schema documentation
- **Performance**: Review indexes and query plans
- **Data Issues**: Verify sample data and constraints
- **Connection Issues**: Check configuration and network settings
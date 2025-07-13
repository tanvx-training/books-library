# History Service

The History Service is responsible for tracking data changes (CREATE, UPDATE, DELETE) across all entities in the library system. It follows Domain-Driven Design (DDD) principles and provides a centralized audit log for the entire application.

## Features

- Records all data changes across the system
- Captures who made the change (user ID and information)
- Stores old and new values for each change
- Tracks specific changes in update operations
- Provides REST API for querying audit logs
- Consumes events from Kafka for asynchronous logging

## Architecture

The service follows a DDD architecture with the following layers:

### Domain Layer
- **Entities**: `AuditLog` as the aggregate root
- **Value Objects**: `AuditLogId`, `ServiceName`, `EntityName`, `EntityId`, `ActionType`, etc.
- **Repository Interfaces**: `AuditLogRepository`
- **Domain Services**: `AuditLogDomainService`

### Application Layer
- **DTOs**: `AuditLogCreateRequest`, `AuditLogResponse`
- **Application Services**: `AuditLogApplicationService`
- **Mappers**: `AuditLogMapper`

### Infrastructure Layer
- **Persistence**: JPA entities, repositories, and mappers
- **Event Handling**: Kafka consumers for audit events
- **Security Configuration**: OAuth2 resource server setup

### Interface Layer
- **REST Controllers**: `AuditLogController`
- **Exception Handling**: Global exception handler

## Database Schema

```sql
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    service_name VARCHAR(100) NOT NULL,      -- Tên service gửi sự kiện (e.g., 'book-service')
    entity_name VARCHAR(100) NOT NULL,       -- Tên thực thể (e.g., 'Book')
    entity_id VARCHAR(255) NOT NULL,         -- ID của record bị thay đổi
    action_type VARCHAR(20) NOT NULL,        -- 'CREATE', 'UPDATE', 'DELETE'
    user_id VARCHAR(36),                     -- ID người dùng từ Keycloak ('sub')
    user_info JSONB,                         -- Thông tin thêm về user (email, name)
    old_value JSONB,                         -- Trạng thái cũ (cho UPDATE, DELETE)
    new_value JSONB,                         -- Trạng thái mới (cho CREATE, UPDATE)
    changes JSONB,                           -- Chỉ lưu các trường thay đổi (tối ưu)
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    request_id VARCHAR(100)                  -- ID để nhóm các log trong cùng 1 request
);

-- Indexes
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_name, entity_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
```

## API Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | /api/history/audit-logs | Create a new audit log | ADMIN |
| GET | /api/history/audit-logs | Get all audit logs | ADMIN |
| GET | /api/history/audit-logs/{id} | Get audit log by ID | ADMIN |
| GET | /api/history/audit-logs/entity/{entityName}/{entityId} | Get audit logs for a specific entity | ADMIN |
| GET | /api/history/audit-logs/user/{userId} | Get audit logs for a specific user | ADMIN |
| GET | /api/history/audit-logs/time-range?start={start}&end={end} | Get audit logs within a time range | ADMIN |
| DELETE | /api/history/audit-logs/{id} | Delete an audit log | ADMIN |

## Event Consumption

The service listens to the following Kafka topics:
- book-service-events
- user-service-events
- lending-service-events

## Security

All endpoints are secured with OAuth2/JWT and require the ADMIN role for access.

## Running the Service

1. Make sure PostgreSQL is running and the database is created
2. Ensure Kafka is running for event consumption
3. Run the service using Maven: `mvn spring-boot:run`
4. The service will be available at http://localhost:8086/api/history 
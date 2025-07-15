# Keycloak Setup Guide for Library Management System

## Overview

This guide provides step-by-step instructions for setting up Keycloak authentication and authorization for the Library Management System microservices architecture.

## Prerequisites

- Docker and Docker Compose installed
- Java 17+
- Maven 3.6+
- PostgreSQL (included in Docker Compose)

## 1. Start Infrastructure Services

### Start PostgreSQL and Keycloak

```bash
# Start PostgreSQL and Keycloak
docker-compose up -d postgres keycloak

# Wait for services to be ready
docker-compose logs -f keycloak
```

### Verify Keycloak is Running

- Access Keycloak Admin Console: http://localhost:8080
- Login with admin/admin credentials

## 2. Configure Keycloak Realm

### Option A: Import Realm Configuration (Recommended)

```bash
# Import the pre-configured realm
docker exec -it keycloak /opt/keycloak/bin/kc.sh import \
  --file /opt/keycloak/data/import/keycloak-realm-config.json
```

### Option B: Manual Configuration

1. **Create Realm**
   - Go to Keycloak Admin Console
   - Click "Create Realm"
   - Name: `library-realm`
   - Click "Create"

2. **Create Roles**
   - Go to Realm roles
   - Create roles: `USER`, `LIBRARIAN`, `ADMIN`

3. **Create Clients**

   **API Gateway Client:**
   - Client ID: `api-gateway`
   - Client authentication: ON
   - Authorization: OFF
   - Standard flow: ON
   - Direct access grants: ON
   - Service accounts roles: ON
   - Valid redirect URIs: `http://localhost:8888/*`
   - Web origins: `http://localhost:8888`

   **User Service Client:**
   - Client ID: `user-service`
   - Client authentication: ON
   - Authorization: OFF
   - Bearer only: ON
   - Service accounts roles: ON

   **Frontend Client:**
   - Client ID: `frontend-app`
   - Client authentication: OFF (Public client)
   - Standard flow: ON
   - Direct access grants: ON
   - Valid redirect URIs: `http://localhost:3000/*`
   - Web origins: `http://localhost:3000`

4. **Create Test Users**
   - Create users with appropriate roles
   - Set passwords (temporary: false)

## 3. Database Setup

### Run Migration Scripts

```bash
# Connect to PostgreSQL
docker exec -it postgres psql -U postgres -d user-service

# Run migration script
\i /docker-entrypoint-initdb.d/keycloak-migration.sql
```

### Verify Database Schema

```sql
-- Check users table structure
\d users;

-- Check if keycloak_id column exists
SELECT column_name, data_type, character_maximum_length 
FROM information_schema.columns 
WHERE table_name = 'users' AND column_name = 'keycloak_id';
```

## 4. Application Configuration

### API Gateway Configuration

Update `api-gateway/src/main/resources/application.yaml`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:8080/realms/library-realm/protocol/openid-connect/certs
          issuer-uri: http://localhost:8080/realms/library-realm
      client:
        registration:
          keycloak:
            client-id: api-gateway
            client-secret: your-client-secret
            authorization-grant-type: authorization_code
            scope: openid, profile, email, roles
        provider:
          keycloak:
            issuer-uri: http://localhost:8080/realms/library-realm
```

### User Service Configuration

Update `user-service/src/main/resources/application.yaml`:

```yaml
keycloak:
  server-url: http://localhost:8080
  realm: library-realm
  admin:
    client-id: admin-cli
    username: admin
    password: admin

spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/library-realm
          jwk-set-uri: http://localhost:8080/realms/library-realm/protocol/openid-connect/certs
```

## 5. Start Services

### Build and Start Services

```bash
# Build all services
mvn clean package -DskipTests

# Start Eureka Server (if using service discovery)
java -jar eureka-server/target/eureka-server-*.jar

# Start API Gateway
java -jar api-gateway/target/api-gateway-*.jar

# Start User Service
java -jar user-service/target/user-service-*.jar
```

### Using Docker Compose (Alternative)

```bash
# Build Docker images
mvn clean package jib:dockerBuild

# Start all services
docker-compose up -d
```

## 6. Testing the Setup

### Test Authentication Flow

1. **Get Access Token**

```bash
curl -X POST http://localhost:8080/realms/library-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=api-gateway" \
  -d "client_secret=your-client-secret" \
  -d "username=user1" \
  -d "password=user123"
```

2. **Test API Gateway**

```bash
# Test protected endpoint
curl -X GET http://localhost:8888/api/auth/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

3. **Test User Service**

```bash
# Test user sync
curl -X POST http://localhost:8888/api/auth/sync/all \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Test User Context

```bash
# Get current user info
curl -X GET http://localhost:8888/api/auth/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

# Check permissions
curl -X GET "http://localhost:8888/api/auth/permissions?role=USER" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 7. Troubleshooting

### Common Issues

1. **Keycloak not accessible**
   - Check if Keycloak container is running: `docker ps`
   - Check logs: `docker-compose logs keycloak`
   - Verify port 8080 is not in use

2. **JWT validation fails**
   - Verify issuer-uri and jwk-set-uri are correct
   - Check if realm name matches configuration
   - Ensure clocks are synchronized

3. **User sync fails**
   - Check Keycloak admin credentials
   - Verify admin-cli client exists
   - Check network connectivity between services

4. **Database connection issues**
   - Verify PostgreSQL is running
   - Check database credentials
   - Ensure database schema is up to date

### Logs to Check

```bash
# Keycloak logs
docker-compose logs keycloak

# API Gateway logs
docker-compose logs api-gateway

# User Service logs
docker-compose logs user-service

# PostgreSQL logs
docker-compose logs postgres
```

## 8. Security Considerations

### Production Setup

1. **Change Default Passwords**
   - Keycloak admin password
   - Database passwords
   - Client secrets

2. **Use HTTPS**
   - Configure SSL certificates
   - Update redirect URIs to use HTTPS

3. **Network Security**
   - Use private networks for service communication
   - Implement proper firewall rules
   - Consider using service mesh for mTLS

4. **Token Security**
   - Configure appropriate token expiration times
   - Implement token refresh mechanism
   - Use secure token storage on client side

### Monitoring

1. **Health Checks**
   - Monitor Keycloak health endpoint
   - Check service availability
   - Monitor database connections

2. **Metrics**
   - Track authentication success/failure rates
   - Monitor token validation performance
   - Track user sync operations

## 9. Next Steps

1. **Frontend Integration**
   - Implement OAuth2 flow in frontend application
   - Handle token refresh
   - Implement logout functionality

2. **Additional Services**
   - Configure other microservices for authentication
   - Implement service-to-service authentication
   - Add authorization policies

3. **Advanced Features**
   - Implement social login
   - Add multi-factor authentication
   - Configure user federation

## Support

For issues and questions:
- Check the troubleshooting section
- Review application logs
- Consult Keycloak documentation
- Contact the development team
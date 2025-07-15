# Authentication API Guide

## Overview

This guide describes the authentication and authorization APIs for the Library Management System, including how to authenticate users, manage user context, and handle permissions.

## Authentication Flow

### 1. User Authentication

The system uses Keycloak for authentication with OAuth2/OpenID Connect protocol.

#### Get Access Token

**Endpoint:** `POST /realms/library-realm/protocol/openid-connect/token`  
**Host:** Keycloak Server (http://localhost:8080)

```bash
curl -X POST http://localhost:8080/realms/library-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=api-gateway" \
  -d "client_secret=kia8wLCbflCup6q8TL5jgLUdcYDivqBb" \
  -d "username=user1" \
  -d "password=user123"
```

**Response:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "not-before-policy": 0,
  "session_state": "uuid",
  "scope": "profile email"
}
```

#### Refresh Token

```bash
curl -X POST http://localhost:8080/realms/library-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=refresh_token" \
  -d "client_id=api-gateway" \
  -d "client_secret=kia8wLCbflCup6q8TL5jgLUdcYDivqBb" \
  -d "refresh_token=YOUR_REFRESH_TOKEN"
```

### 2. Using Access Token

All API requests to protected endpoints must include the access token in the Authorization header:

```bash
curl -X GET http://localhost:8888/api/users/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## API Gateway Endpoints

### Authentication Status

#### Get Current User Information

**Endpoint:** `GET /api/auth/me`  
**Authentication:** Required  
**Roles:** Any authenticated user

```bash
curl -X GET http://localhost:8888/api/auth/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Response:**
```json
{
  "keycloakId": "uuid-from-keycloak",
  "username": "user1",
  "email": "user1@library.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "roles": ["USER"],
  "permissions": ["read:books", "borrow:books"],
  "isActive": true
}
```

#### Check User Permissions

**Endpoint:** `GET /api/auth/permissions`  
**Authentication:** Required  
**Query Parameters:**
- `role` (optional): Role to check
- `permission` (optional): Permission to check

```bash
# Check if user has specific role
curl -X GET "http://localhost:8888/api/auth/permissions?role=LIBRARIAN" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

# Check if user has specific permission
curl -X GET "http://localhost:8888/api/auth/permissions?permission=write:books" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Response:**
```json
{
  "hasRole": true,
  "hasPermission": false,
  "roles": ["USER", "LIBRARIAN"],
  "permissions": ["read:books", "borrow:books"]
}
```

### User Synchronization

#### Sync Single User from Keycloak

**Endpoint:** `POST /api/auth/sync/{keycloakId}`  
**Authentication:** Required  
**Roles:** ADMIN, LIBRARIAN

```bash
curl -X POST http://localhost:8888/api/auth/sync/uuid-from-keycloak \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Response:**
```json
{
  "message": "User synced successfully",
  "keycloakId": "uuid-from-keycloak",
  "username": "synced-user"
}
```

#### Sync All Users from Keycloak

**Endpoint:** `POST /api/auth/sync/all`  
**Authentication:** Required  
**Roles:** ADMIN

```bash
curl -X POST http://localhost:8888/api/auth/sync/all \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Response:**
```json
{
  "message": "All users synced successfully"
}
```

### Health Check

**Endpoint:** `GET /api/auth/health`  
**Authentication:** Not required

```bash
curl -X GET http://localhost:8888/api/auth/health
```

**Response:**
```json
{
  "status": "UP",
  "service": "user-service-auth"
}
```

## User Service Endpoints

### User Management

#### Get User Profile

**Endpoint:** `GET /api/users/me`  
**Authentication:** Required

```bash
curl -X GET http://localhost:8888/api/users/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

#### Update User Profile

**Endpoint:** `PUT /api/users/me`  
**Authentication:** Required

```bash
curl -X PUT http://localhost:8888/api/users/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Updated",
    "lastName": "Name",
    "phone": "+1234567890"
  }'
```

#### Get All Users (Admin/Librarian)

**Endpoint:** `GET /api/users`  
**Authentication:** Required  
**Roles:** ADMIN, LIBRARIAN

```bash
curl -X GET http://localhost:8888/api/users \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

#### Create User (Admin)

**Endpoint:** `POST /api/users`  
**Authentication:** Required  
**Roles:** ADMIN

```bash
curl -X POST http://localhost:8888/api/users \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@library.com",
    "firstName": "New",
    "lastName": "User",
    "phone": "+1234567890"
  }'
```

### Library Card Management

#### Get User's Library Cards

**Endpoint:** `GET /api/library-cards/my-cards`  
**Authentication:** Required

```bash
curl -X GET http://localhost:8888/api/library-cards/my-cards \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

#### Get All Library Cards (Admin/Librarian)

**Endpoint:** `GET /api/library-cards`  
**Authentication:** Required  
**Roles:** ADMIN, LIBRARIAN

```bash
curl -X GET http://localhost:8888/api/library-cards \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## Error Responses

### Authentication Errors

#### 401 Unauthorized
```json
{
  "error": "Authentication Error",
  "message": "Invalid or expired token"
}
```

#### 403 Forbidden
```json
{
  "error": "Access Denied",
  "message": "Insufficient permissions"
}
```

### Validation Errors

#### 400 Bad Request
```json
{
  "error": "Validation Error",
  "message": "Invalid request data",
  "details": [
    {
      "field": "email",
      "message": "Invalid email format"
    }
  ]
}
```

### Server Errors

#### 500 Internal Server Error
```json
{
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```

## Role-Based Access Control

### Roles

1. **USER**
   - Basic library user
   - Can view own profile and library cards
   - Can borrow and return books

2. **LIBRARIAN**
   - Library staff member
   - Can manage users and library cards
   - Can manage books and borrowings
   - Can view reports

3. **ADMIN**
   - System administrator
   - Full access to all resources
   - Can manage system configuration
   - Can create and delete users

### Permission Matrix

| Endpoint | USER | LIBRARIAN | ADMIN |
|----------|------|-----------|-------|
| GET /api/auth/me | ✓ | ✓ | ✓ |
| GET /api/auth/permissions | ✓ | ✓ | ✓ |
| POST /api/auth/sync/{id} | ✗ | ✓ | ✓ |
| POST /api/auth/sync/all | ✗ | ✗ | ✓ |
| GET /api/users/me | ✓ | ✓ | ✓ |
| PUT /api/users/me | ✓ | ✓ | ✓ |
| GET /api/users | ✗ | ✓ | ✓ |
| POST /api/users | ✗ | ✗ | ✓ |
| DELETE /api/users/{id} | ✗ | ✗ | ✓ |
| GET /api/library-cards/my-cards | ✓ | ✓ | ✓ |
| GET /api/library-cards | ✗ | ✓ | ✓ |
| POST /api/library-cards | ✗ | ✓ | ✓ |

## Headers

### Request Headers

All authenticated requests must include:

```
Authorization: Bearer YOUR_ACCESS_TOKEN
Content-Type: application/json (for POST/PUT requests)
```

### Response Headers from API Gateway

The API Gateway adds these headers for downstream services:

```
X-User-Id: keycloak-user-id
X-Username: username
X-User-Email: user@example.com
X-User-Roles: USER,LIBRARIAN
X-User-Permissions: read:books,write:books
```

## Rate Limiting

### Default Limits

- Authentication endpoints: 10 requests per minute per IP
- User management endpoints: 100 requests per minute per user
- Sync endpoints: 5 requests per minute per user

### Rate Limit Headers

```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1640995200
```

## Best Practices

### Token Management

1. **Store tokens securely** (HttpOnly cookies for web apps)
2. **Implement token refresh** before expiration
3. **Handle token expiration** gracefully
4. **Clear tokens on logout**

### Error Handling

1. **Check response status codes**
2. **Handle authentication errors** (redirect to login)
3. **Handle authorization errors** (show access denied)
4. **Implement retry logic** for transient errors

### Security

1. **Use HTTPS** in production
2. **Validate SSL certificates**
3. **Implement CSRF protection**
4. **Log security events**

## Examples

### Complete Authentication Flow

```javascript
// 1. Login
const loginResponse = await fetch('http://localhost:8080/realms/library-realm/protocol/openid-connect/token', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded',
  },
  body: new URLSearchParams({
    grant_type: 'password',
    client_id: 'api-gateway',
    client_secret: 'kia8wLCbflCup6q8TL5jgLUdcYDivqBb',
    username: 'user1',
    password: 'user123'
  })
});

const tokens = await loginResponse.json();

// 2. Use access token
const userResponse = await fetch('http://localhost:8888/api/auth/me', {
  headers: {
    'Authorization': `Bearer ${tokens.access_token}`
  }
});

const user = await userResponse.json();
console.log('Current user:', user);

// 3. Check permissions
const permissionResponse = await fetch('http://localhost:8888/api/auth/permissions?role=LIBRARIAN', {
  headers: {
    'Authorization': `Bearer ${tokens.access_token}`
  }
});

const permissions = await permissionResponse.json();
console.log('Has librarian role:', permissions.hasRole);
```

### Error Handling Example

```javascript
async function makeAuthenticatedRequest(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: {
      'Authorization': `Bearer ${getAccessToken()}`,
      'Content-Type': 'application/json',
      ...options.headers
    }
  });

  if (response.status === 401) {
    // Token expired, try to refresh
    await refreshToken();
    // Retry request
    return makeAuthenticatedRequest(url, options);
  }

  if (response.status === 403) {
    // Access denied
    throw new Error('Access denied');
  }

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }

  return response.json();
}
```
# JWT Authentication Migration Guide - Book Service

## Overview

This document describes the migration from UserContextService-based authentication to direct JWT token authentication using Keycloak tokens relayed by the API Gateway.

## Migration Summary

### What Was Changed

#### 1. Removed UserContextService
- **Deleted**: `UserContextService.java` - Custom service for managing user context from headers
- **Reason**: No longer needed as we now use JWT tokens directly from Keycloak

#### 2. Created JWT Authentication Infrastructure

**New Files Created**:

1. **`JwtAuthenticationService.java`** - Core JWT authentication service
   - Extracts user information directly from JWT tokens
   - Provides role and permission checking methods
   - Handles Keycloak-specific JWT claims

2. **Security Annotations**:
   - `@RequireAuthentication` - Requires user to be authenticated
   - `@RequireBookManagement` - Requires ADMIN or LIBRARIAN role
   - `@RequireRole` - Requires specific roles

#### 3. Updated All Controllers

**Controllers Updated**:
- `AuthorController.java`
- `BookController.java` 
- `BookCopyController.java`
- `CategoryController.java`
- `PublisherController.java`

**Changes Made**:
- Removed `UserContextService` dependency
- Added `JwtAuthenticationService` dependency
- Replaced manual permission checks with security annotations
- Updated method signatures to use `AuthenticatedUser` instead of `UserContext`

#### 4. Updated All Application Services

**Services Updated**:
- `AuthorApplicationService.java`
- `BookApplicationService.java`
- `BookCopyApplicationService.java`

**Changes Made**:
- Updated method signatures to accept `AuthenticatedUser` instead of `UserContext`
- Fixed permission validation logic
- Updated logging statements

## Technical Details

### JWT Authentication Flow

```
1. Client → API Gateway (with JWT token)
2. API Gateway → Book Service (TokenRelay forwards JWT)
3. Book Service → Spring Security (validates JWT)
4. Spring Security → JwtAuthenticationService (extracts user info)
5. JwtAuthenticationService → Controllers/Services (provides user context)
```

### User Information Extraction

The `JwtAuthenticationService.AuthenticatedUser` class provides:

```java
public static class AuthenticatedUser {
    private String keycloakId;        // From 'sub' claim
    private String username;          // From 'preferred_username' claim
    private String email;             // From 'email' claim
    private String firstName;         // From 'given_name' claim
    private String lastName;          // From 'family_name' claim
    private String fullName;          // From 'name' claim
    private Set<String> roles;        // From realm_access.roles
    private Set<String> authorities;  // Spring Security authorities
    private Map<String, Object> customClaims; // Additional JWT claims
}
```

### Role-Based Authorization

#### Method-Level Security

```java
// Requires authentication
@RequireAuthentication
public ResponseEntity<List<BookCopy>> getMyBorrowedBooks() { ... }

// Requires ADMIN or LIBRARIAN role
@RequireBookManagement
public ResponseEntity<Book> createBook(@RequestBody BookRequest request) { ... }

// Custom role requirements
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deleteBook(@PathVariable Long id) { ... }
```

#### Programmatic Security Checks

```java
// In controllers
JwtAuthenticationService.AuthenticatedUser currentUser = jwtAuthenticationService.getCurrentUser();

// In services
if (currentUser == null || !currentUser.canManageBooks()) {
    throw new BookApplicationException("User does not have permission to create books");
}
```

### Security Configuration

The existing `SecurityConfig.java` already handles JWT validation:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Enables @PreAuthorize annotations
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );
        return http.build();
    }
    
    // Custom converter extracts roles from Keycloak JWT
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }
}
```

## API Changes

### Before (UserContextService)

```java
@PostMapping
public ResponseEntity<BookResponse> createBook(
        @RequestBody BookCreateRequest request,
        HttpServletRequest httpRequest) {
    
    UserContextService.UserContext userContext = 
        (UserContextService.UserContext) httpRequest.getAttribute("userContext");
    
    if (userContext == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    return ResponseEntity.ok(ApiResponse.success(
        bookApplicationService.createBook(request, userContext)
    ));
}
```

### After (JWT Authentication)

```java
@PostMapping
@RequireBookManagement  // Declarative security
public ResponseEntity<BookResponse> createBook(
        @RequestBody BookCreateRequest request) {
    
    AuthenticatedUser currentUser = jwtAuthenticationService.getCurrentUser();
    
    return ResponseEntity.ok(ApiResponse.success(
        bookApplicationService.createBook(request, currentUser)
    ));
}
```

## Benefits of the New Approach

### 1. **Simplified Code**
- No manual header parsing
- No manual user context creation
- Declarative security with annotations

### 2. **Better Security**
- Direct JWT validation by Spring Security
- Automatic token expiration handling
- Standard OAuth2/OIDC compliance

### 3. **Improved Maintainability**
- Less custom authentication code
- Standard Spring Security patterns
- Centralized security configuration

### 4. **Enhanced Features**
- Access to all JWT claims
- Automatic role extraction
- Support for custom claims

### 5. **Better Integration**
- Seamless API Gateway TokenRelay integration
- Standard Keycloak JWT format support
- Compatible with Spring Security ecosystem

## Configuration Requirements

### API Gateway Configuration

Ensure TokenRelay is configured in the API Gateway:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: book-service
          uri: http://book-service:8081
          predicates:
            - Path=/api/v1/books/**,/api/book-copies/**
          filters:
            - TokenRelay  # This forwards the JWT token
```

### Keycloak Configuration

Ensure the following claims are included in JWT tokens:
- `sub` (subject/user ID)
- `preferred_username`
- `email`
- `given_name`
- `family_name`
- `name`
- `realm_access.roles`

### Book Service Configuration

The existing OAuth2 resource server configuration works:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/library-realm
          jwk-set-uri: http://localhost:8080/realms/library-realm/protocol/openid-connect/certs
```

## Testing the Migration

### 1. **Authentication Test**
```bash
# Get JWT token from Keycloak
TOKEN=$(curl -X POST "http://localhost:8080/realms/library-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&client_id=book-service&username=testuser&password=password")

# Test authenticated endpoint
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/books
```

### 2. **Authorization Test**
```bash
# Test endpoint requiring LIBRARIAN role
curl -H "Authorization: Bearer $TOKEN" \
  -X POST http://localhost:8080/api/v1/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Book","isbn":"123456789","publisherId":1,"authorIds":[1],"categoryIds":[1]}'
```

### 3. **User Context Test**
```bash
# Test user-specific endpoint
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/book-copies/my-borrowed
```

## Troubleshooting

### Common Issues

1. **401 Unauthorized**
   - Check JWT token validity
   - Verify Keycloak issuer URI
   - Ensure API Gateway TokenRelay is working

2. **403 Forbidden**
   - Check user roles in Keycloak
   - Verify role mapping in JWT token
   - Check method-level security annotations

3. **Missing User Information**
   - Verify JWT claims configuration in Keycloak
   - Check claim extraction in `JwtAuthenticationService`
   - Ensure all required claims are present

### Debug Configuration

Enable debug logging:

```yaml
logging:
  level:
    org.springframework.security: DEBUG
    com.library.book.infrastructure.security: DEBUG
```

## Migration Checklist

- [x] Remove UserContextService
- [x] Create JwtAuthenticationService
- [x] Create security annotations
- [x] Update all controllers
- [x] Update all application services
- [x] Test compilation
- [x] Update documentation

## Conclusion

The migration from UserContextService to direct JWT authentication provides:

- **Cleaner Architecture**: Standard Spring Security patterns
- **Better Security**: Direct JWT validation and automatic token handling
- **Improved Maintainability**: Less custom code, more declarative security
- **Enhanced Integration**: Seamless API Gateway and Keycloak integration

The book-service now uses industry-standard JWT authentication while maintaining all existing functionality and improving security posture.
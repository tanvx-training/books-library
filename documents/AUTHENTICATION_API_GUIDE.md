# Authentication & Authorization API Implementation Guide

## Tổng Quan

Hướng dẫn chi tiết cách implement authentication và authorization trong Library Management System sử dụng Keycloak và Spring Security.

## 1. Cấu Hình Security trong API Gateway

### SecurityConfig Implementation

```java
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
            .authorizeExchange(exchanges -> exchanges
                
                // ============= PUBLIC ENDPOINTS =============
                .pathMatchers("/actuator/**", "/health/**").permitAll()
                .pathMatchers("/auth/**", "/api/auth/**").permitAll()
                .pathMatchers("/login/oauth2/code/**", "/oauth2/**").permitAll()
                
                // Public book information (read-only)
                .pathMatchers("GET", "/api/books/public/**").permitAll()
                .pathMatchers("GET", "/api/books/search").permitAll()
                .pathMatchers("GET", "/api/books/{id}/details").permitAll()
                
                // ============= USER ENDPOINTS =============
                .pathMatchers("GET", "/api/users/me").hasRole("USER")
                .pathMatchers("PUT", "/api/users/me").hasRole("USER")
                .pathMatchers("GET", "/api/users/me/**").hasRole("USER")
                .pathMatchers("POST", "/api/users/me/**").hasRole("USER")
                .pathMatchers("PUT", "/api/users/me/**").hasRole("USER")
                .pathMatchers("DELETE", "/api/users/me/reservations/*").hasRole("USER")
                
                // Book browsing for authenticated users
                .pathMatchers("GET", "/api/books").hasRole("USER")
                .pathMatchers("GET", "/api/books/{id}").hasRole("USER")
                .pathMatchers("GET", "/api/books/{id}/availability").hasRole("USER")
                .pathMatchers("GET", "/api/book-copies/book/{bookId}").hasRole("USER")
                
                // Catalog browsing
                .pathMatchers("GET", "/api/categories").hasRole("USER")
                .pathMatchers("GET", "/api/authors").hasRole("USER")
                .pathMatchers("GET", "/api/publishers").hasRole("USER")
                
                // ============= LIBRARIAN ENDPOINTS =============
                // User management
                .pathMatchers("GET", "/api/users").hasRole("LIBRARIAN")
                .pathMatchers("GET", "/api/users/{id}").hasRole("LIBRARIAN")
                .pathMatchers("POST", "/api/users").hasRole("LIBRARIAN")
                .pathMatchers("PUT", "/api/users/{id}").hasRole("LIBRARIAN")
                
                // Library card management
                .pathMatchers("POST", "/api/users/{id}/library-cards").hasRole("LIBRARIAN")
                .pathMatchers("GET", "/api/library-cards/**").hasRole("LIBRARIAN")
                .pathMatchers("PUT", "/api/library-cards/**").hasRole("LIBRARIAN")
                
                // Book management
                .pathMatchers("POST", "/api/books").hasRole("LIBRARIAN")
                .pathMatchers("PUT", "/api/books/{id}").hasRole("LIBRARIAN")
                .pathMatchers("POST", "/api/book-copies").hasRole("LIBRARIAN")
                .pathMatchers("PUT", "/api/book-copies/**").hasRole("LIBRARIAN")
                
                // Lending operations
                .pathMatchers("POST", "/api/lending/**").hasRole("LIBRARIAN")
                .pathMatchers("GET", "/api/lending/**").hasRole("LIBRARIAN")
                
                // Catalog management
                .pathMatchers("POST", "/api/categories").hasRole("LIBRARIAN")
                .pathMatchers("PUT", "/api/categories/{id}").hasRole("LIBRARIAN")
                .pathMatchers("POST", "/api/authors").hasRole("LIBRARIAN")
                .pathMatchers("PUT", "/api/authors/{id}").hasRole("LIBRARIAN")
                .pathMatchers("POST", "/api/publishers").hasRole("LIBRARIAN")
                .pathMatchers("PUT", "/api/publishers/{id}").hasRole("LIBRARIAN")
                
                // Notifications
                .pathMatchers("POST", "/api/notifications/**").hasRole("LIBRARIAN")
                .pathMatchers("GET", "/api/notifications/**").hasRole("LIBRARIAN")
                
                // History and audit (read)
                .pathMatchers("GET", "/api/history/**").hasRole("LIBRARIAN")
                
                // File management
                .pathMatchers("POST", "/api/files/upload").hasAnyRole("LIBRARIAN", "ADMIN")
                .pathMatchers("DELETE", "/api/files/{fileId}").hasAnyRole("LIBRARIAN", "ADMIN")
                
                // ============= ADMIN ENDPOINTS =============
                // User administration
                .pathMatchers("DELETE", "/api/users/{id}").hasRole("ADMIN")
                .pathMatchers("POST", "/api/users/{id}/suspend").hasRole("ADMIN")
                .pathMatchers("POST", "/api/users/{id}/reactivate").hasRole("ADMIN")
                .pathMatchers("PUT", "/api/users/{id}/roles").hasRole("ADMIN")
                .pathMatchers("POST", "/api/users/{id}/roles/*").hasRole("ADMIN")
                .pathMatchers("DELETE", "/api/users/{id}/roles/*").hasRole("ADMIN")
                
                // System administration
                .pathMatchers("/api/admin/**").hasRole("ADMIN")
                .pathMatchers("GET", "/api/system/**").hasRole("ADMIN")
                .pathMatchers("POST", "/api/system/**").hasRole("ADMIN")
                
                // Delete operations (admin only)
                .pathMatchers("DELETE", "/api/books/{id}").hasRole("ADMIN")
                .pathMatchers("DELETE", "/api/book-copies/{id}").hasRole("ADMIN")
                .pathMatchers("DELETE", "/api/categories/{id}").hasRole("ADMIN")
                .pathMatchers("DELETE", "/api/authors/{id}").hasRole("ADMIN")
                .pathMatchers("DELETE", "/api/publishers/{id}").hasRole("ADMIN")
                .pathMatchers("DELETE", "/api/library-cards/{id}").hasRole("ADMIN")
                .pathMatchers("DELETE", "/api/lending/{id}").hasRole("ADMIN")
                .pathMatchers("DELETE", "/api/notifications/{id}").hasRole("ADMIN")
                .pathMatchers("DELETE", "/api/history/**").hasRole("ADMIN")
                
                // Bulk operations
                .pathMatchers("POST", "/api/*/admin/bulk-*").hasRole("ADMIN")
                .pathMatchers("POST", "/api/*/admin/merge").hasRole("ADMIN")
                .pathMatchers("POST", "/api/*/admin/force-*").hasRole("ADMIN")
                
                // Configuration
                .pathMatchers("GET", "/api/config/**").hasRole("ADMIN")
                .pathMatchers("PUT", "/api/config/**").hasRole("ADMIN")
                .pathMatchers("POST", "/api/config/reload").hasRole("ADMIN")
                
                // ============= SERVICE COMMUNICATION =============
                .pathMatchers("/internal/**").hasRole("SERVICE")
                
                // ============= SPECIAL CASES =============
                .pathMatchers("POST", "/webhooks/**").permitAll()
                .pathMatchers("GET", "/api/files/{fileId}").hasRole("USER")
                
                // Default: require authentication
                .anyExchange().authenticated()
            )
            .oauth2Login(oauth2 -> {})
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtDecoder(reactiveJwtDecoder())
                    .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                )
            )
            .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);
    }
}
```

## 2. JWT Token Processing

### JWT Decoder Configuration

```java
@Bean
public ReactiveJwtDecoder reactiveJwtDecoder() {
    NimbusReactiveJwtDecoder jwtDecoder = NimbusReactiveJwtDecoder
        .withJwkSetUri("http://localhost:8080/realms/library/protocol/openid-connect/certs")
        .build();
    
    jwtDecoder.setJwtValidator(jwtValidator());
    return jwtDecoder;
}

@Bean
public Validator<Jwt> jwtValidator() {
    List<Validator<Jwt>> validators = new ArrayList<>();
    validators.add(new JwtTimestampValidator());
    validators.add(new JwtIssuerValidator("http://localhost:8080/realms/library"));
    return new DelegatingValidator<>(validators);
}
```

### Authorities Extractor

```java
@Bean
public ReactiveJwtAuthenticationConverterAdapter grantedAuthoritiesExtractor() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        
        // Extract roles from realm_access.roles
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
            }
        }
        
        // Extract roles from resource_access
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            Map<String, Object> libraryClient = (Map<String, Object>) resourceAccess.get("library-client");
            if (libraryClient != null && libraryClient.containsKey("roles")) {
                Collection<String> clientRoles = (Collection<String>) libraryClient.get("roles");
                for (String role : clientRoles) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                }
            }
        }
        
        return authorities;
    });
    
    return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
}
```

## 3. Method-Level Security

### Service Layer Security

```java
@Service
@PreAuthorize("hasRole('LIBRARIAN')")
public class UserManagementApplicationService {
    
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        // Implementation
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        // Implementation
    }
    
    @PostAuthorize("hasRole('ADMIN') or returnObject.userId == authentication.principal.userId")
    public UserDetailResponse getUserById(Long userId) {
        // Implementation
    }
}
```

### Controller Security

```java
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('USER')")
public class UserController {
    
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String userId = authentication.getName();
        // Implementation
    }
    
    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        // Implementation
    }
    
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        // Implementation
    }
}
```

## 4. User Context Extraction

### User Context Service

```java
@Service
public class UserContextService {
    
    public UserContext getCurrentUserContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;
            Jwt jwt = jwtToken.getToken();
            
            return UserContext.builder()
                .userId(jwt.getSubject())
                .email(jwt.getClaimAsString("email"))
                .firstName(jwt.getClaimAsString("given_name"))
                .lastName(jwt.getClaimAsString("family_name"))
                .roles(extractRoles(jwt))
                .build();
        }
        
        throw new SecurityException("No valid authentication found");
    }
    
    private Set<String> extractRoles(Jwt jwt) {
        Set<String> roles = new HashSet<>();
        
        // Extract from realm_access
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            Collection<String> realmRoles = (Collection<String>) realmAccess.get("roles");
            roles.addAll(realmRoles);
        }
        
        return roles;
    }
}
```

### User Context Filter

```java
@Component
public class UserContextFilter implements WebFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getPrincipal()
            .cast(JwtAuthenticationToken.class)
            .map(JwtAuthenticationToken::getToken)
            .map(this::extractUserContext)
            .doOnNext(userContext -> {
                // Set user context in MDC or thread local
                MDC.put("userId", userContext.getUserId());
                MDC.put("userEmail", userContext.getEmail());
            })
            .then(chain.filter(exchange))
            .doFinally(signalType -> MDC.clear());
    }
    
    private UserContext extractUserContext(Jwt jwt) {
        return UserContext.builder()
            .userId(jwt.getSubject())
            .email(jwt.getClaimAsString("email"))
            .firstName(jwt.getClaimAsString("given_name"))
            .lastName(jwt.getClaimAsString("family_name"))
            .build();
    }
}
```

## 5. Error Handling

### Security Exception Handler

```java
@ControllerAdvice
public class SecurityExceptionHandler {
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .error("FORBIDDEN")
            .message("Insufficient privileges to access this resource")
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .error("UNAUTHORIZED")
            .message("Authentication required")
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .error("INVALID_TOKEN")
            .message("Invalid or expired token")
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
```

## 6. Testing Security

### Security Test Configuration

```java
@TestConfiguration
public class SecurityTestConfig {
    
    @Bean
    @Primary
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder
            .withSecretKey(getSecretKey())
            .macAlgorithm(MacAlgorithm.HS256)
            .build();
    }
    
    private SecretKey getSecretKey() {
        return new SecretKeySpec("test-secret-key".getBytes(), "HmacSHA256");
    }
}
```

### Integration Tests

```java
@SpringBootTest
@AutoConfigureWebTestClient
class SecurityIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Test
    void shouldAllowPublicAccess() {
        webTestClient.get()
            .uri("/api/books/public/search")
            .exchange()
            .expectStatus().isOk();
    }
    
    @Test
    void shouldRequireAuthenticationForUserEndpoints() {
        webTestClient.get()
            .uri("/api/users/me")
            .exchange()
            .expectStatus().isUnauthorized();
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void shouldAllowUserAccessWithValidRole() {
        webTestClient.get()
            .uri("/api/users/me")
            .exchange()
            .expectStatus().isOk();
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyLibrarianEndpointsForUser() {
        webTestClient.post()
            .uri("/api/users")
            .exchange()
            .expectStatus().isForbidden();
    }
    
    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void shouldAllowLibrarianAccess() {
        webTestClient.get()
            .uri("/api/users")
            .exchange()
            .expectStatus().isOk();
    }
}
```

## 7. Frontend Integration

### Token Storage

```javascript
// Store JWT token
localStorage.setItem('access_token', token);
localStorage.setItem('refresh_token', refreshToken);

// Add token to requests
const token = localStorage.getItem('access_token');
axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
```

### Role-based UI

```javascript
// Check user roles
const userRoles = parseJwt(token).realm_access.roles;

const isAdmin = userRoles.includes('ADMIN');
const isLibrarian = userRoles.includes('LIBRARIAN');
const isUser = userRoles.includes('USER');

// Conditional rendering
{isLibrarian && <LibrarianPanel />}
{isAdmin && <AdminPanel />}
```

### API Error Handling

```javascript
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response.status === 401) {
      // Token expired, redirect to login
      window.location.href = '/login';
    } else if (error.response.status === 403) {
      // Insufficient privileges
      showErrorMessage('You do not have permission to perform this action');
    }
    return Promise.reject(error);
  }
);
```

## 8. Monitoring & Logging

### Security Events Logging

```java
@Component
public class SecurityEventLogger {
    
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        securityLogger.info("Authentication successful for user: {}", username);
    }
    
    @EventListener
    public void handleAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        securityLogger.warn("Authentication failed for user: {}", username);
    }
    
    @EventListener
    public void handleAccessDenied(AuthorizationDeniedEvent event) {
        String username = event.getAuthentication().getName();
        securityLogger.warn("Access denied for user: {} to resource: {}", 
            username, event.getAuthorizationDecision());
    }
}
```

## 9. Best Practices

### 1. Token Validation
- Always validate JWT signature
- Check token expiration
- Verify issuer and audience

### 2. Role Hierarchy
- Use role hierarchy (ADMIN > LIBRARIAN > USER)
- Implement principle of least privilege
- Regular role audits

### 3. Security Headers
```java
@Bean
public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
        .headers(headers -> headers
            .frameOptions().deny()
            .contentTypeOptions().and()
            .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                .maxAgeInSeconds(31536000)
                .includeSubdomains(true)
            )
        )
        // ... other configuration
        .build();
}
```

### 4. Rate Limiting
```java
@Component
public class RateLimitingFilter implements WebFilter {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String clientId = getClientId(exchange);
        String key = "rate_limit:" + clientId;
        
        return Mono.fromCallable(() -> redisTemplate.opsForValue().increment(key))
            .flatMap(count -> {
                if (count == 1) {
                    redisTemplate.expire(key, Duration.ofMinutes(1));
                }
                
                if (count > getRateLimit(exchange)) {
                    return handleRateLimitExceeded(exchange);
                }
                
                return chain.filter(exchange);
            });
    }
}
```

## 10. Troubleshooting

### Common Issues

1. **Token Expired**
   - Implement token refresh mechanism
   - Handle 401 responses gracefully

2. **Role Not Found**
   - Check Keycloak role mapping
   - Verify JWT token structure

3. **CORS Issues**
   - Configure CORS properly
   - Check preflight requests

4. **Method Security Not Working**
   - Enable method security with `@EnableGlobalMethodSecurity`
   - Check SpEL expressions

### Debug Configuration

```yaml
logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.web.cors: DEBUG
    com.library.gateway.security: DEBUG
```

---

*Tài liệu này cung cấp hướng dẫn chi tiết để implement authentication và authorization trong Library Management System. Cập nhật thường xuyên khi có thay đổi.*
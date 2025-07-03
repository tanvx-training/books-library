# OAuth2/OIDC Security Best Practices for Microservices

This document outlines security best practices for implementing OAuth2/OIDC with JWT and Keycloak in a Spring Boot microservices architecture.

## 1. Token Security

### 1.1. Token Lifespans

- **Access Token**: Keep short-lived (5-15 minutes) to minimize the risk if compromised.
- **Refresh Token**: Can be longer-lived (days/weeks) but should be stored securely.
- **ID Token**: Used only for authentication, not for authorization. Don't send to APIs.

### 1.2. Token Storage

- **Frontend (Browser)**:
  - **DO NOT** store tokens in localStorage or sessionStorage (vulnerable to XSS).
  - Store tokens in memory (variables) for SPA applications.
  - Use HttpOnly cookies for web applications when possible.
  - For mobile apps, use secure storage mechanisms (Keychain for iOS, KeyStore for Android).

- **Backend**:
  - Never log tokens in plain text.
  - Don't store tokens in databases unless absolutely necessary.
  - If storing refresh tokens, use strong encryption.

### 1.3. Token Validation

- Always validate token signature using the public key from Keycloak.
- Validate the issuer (`iss` claim) to ensure it's from your trusted Keycloak instance.
- Validate the audience (`aud` claim) to ensure the token is intended for your service.
- Check token expiration (`exp` claim) to prevent usage of expired tokens.
- Validate the token's not-before time (`nbf` claim) if present.

## 2. API Gateway Security

### 2.1. Authentication at the Gateway

- Authenticate all requests at the API Gateway level.
- Extract user information and pass it to downstream services via headers.
- Sign or encrypt headers containing user information to prevent tampering.

### 2.2. Propagating Identity

- Use custom headers (e.g., `X-User-Id`, `X-User-Roles`) to propagate identity.
- Consider using the `TokenRelay` filter in Spring Cloud Gateway to forward the token.
- For service-to-service communication, consider using client credentials flow.

### 2.3. Rate Limiting and Throttling

- Implement rate limiting at the API Gateway to prevent abuse.
- Apply different rate limits based on user roles or subscription levels.
- Use circuit breakers to prevent cascading failures.

## 3. Microservice Security

### 3.1. Defense in Depth

- Even though the Gateway authenticates requests, each microservice should still validate tokens.
- Implement fine-grained authorization at the service level using `@PreAuthorize`.
- Use method-level security for sensitive operations.

### 3.2. Service-to-Service Communication

- Use client credentials flow for service-to-service communication.
- Consider using mutual TLS (mTLS) for additional security.
- Implement network-level security (firewalls, security groups) to restrict direct access to services.

### 3.3. Least Privilege Principle

- Create specific service accounts with minimal permissions for each microservice.
- Use different client credentials for different services.
- Regularly audit and rotate service account credentials.

## 4. Keycloak Configuration

### 4.1. Realm Settings

- Use separate realms for different environments (dev, test, prod).
- Configure appropriate token lifespans based on security requirements.
- Enable Brute Force Protection to prevent password guessing attacks.

### 4.2. Client Configuration

- Use confidential clients with client secrets for backend services.
- Use public clients for frontend applications.
- Restrict redirect URIs to only trusted applications.
- Limit the scopes and roles assigned to each client.
- Enable consent screen for third-party applications.

### 4.3. User Management

- Implement strong password policies.
- Enable multi-factor authentication for sensitive operations.
- Use email verification for new user registrations.
- Implement account lockout policies.

## 5. Role-Based Access Control (RBAC)

### 5.1. Role Design

- Use realm roles for coarse-grained permissions.
- Use client roles for fine-grained, application-specific permissions.
- Consider using groups to manage collections of users with similar permissions.

### 5.2. Role Mapping

- Map Keycloak roles to Spring Security authorities correctly.
- Prefix roles with `ROLE_` in Spring Security as per convention.
- Use SpEL expressions in `@PreAuthorize` annotations for complex authorization rules.

### 5.3. Role Checking

- Use method-level security with `@PreAuthorize` for fine-grained control.
- Implement custom permission evaluators for complex authorization logic.
- Log authorization failures for auditing purposes.

## 6. HTTPS and TLS

- Use HTTPS for all communication, including internal service-to-service calls.
- Configure proper TLS settings (TLS 1.2+ only, strong ciphers).
- Use valid certificates from trusted CAs or properly managed internal CAs.
- Implement HTTP Strict Transport Security (HSTS).

## 7. Logging and Monitoring

### 7.1. Security Logging

- Log authentication events (success and failures).
- Log authorization decisions for sensitive operations.
- Never log sensitive data (tokens, passwords, personal information).
- Include correlation IDs in logs for request tracing.

### 7.2. Monitoring

- Monitor failed authentication attempts.
- Set up alerts for unusual patterns (e.g., sudden increase in token validation failures).
- Monitor Keycloak's health and performance.
- Implement distributed tracing for security-related events.

## 8. Token Revocation and Logout

### 8.1. Logout Handling

- Implement proper logout procedures that invalidate tokens.
- Clear tokens from client storage on logout.
- Call Keycloak's logout endpoint to invalidate the session.

### 8.2. Token Revocation

- Implement token revocation checks for sensitive operations.
- Consider using Keycloak's token introspection endpoint for high-security operations.
- Implement a blacklist for revoked tokens if necessary.

## 9. Error Handling

- Return appropriate HTTP status codes (401 for authentication failures, 403 for authorization failures).
- Don't leak sensitive information in error messages.
- Implement consistent error handling across all microservices.
- Log security exceptions with appropriate context but without sensitive data.

## 10. Regular Security Audits

- Regularly review and rotate client secrets and service account credentials.
- Audit role assignments and permissions.
- Review token lifespans and adjust based on security requirements.
- Conduct penetration testing on your authentication and authorization mechanisms.

## 11. Development Practices

- Use security libraries and frameworks rather than implementing security features from scratch.
- Keep dependencies up to date to address security vulnerabilities.
- Follow the principle of least privilege in development and operations.
- Implement secure coding practices and conduct code reviews with a security focus.

## 12. Handling Sensitive Data

- Encrypt sensitive data at rest and in transit.
- Implement data masking for PII in logs and responses.
- Use Keycloak's user attributes for storing user-specific sensitive information.
- Implement proper access controls for sensitive data endpoints.

By following these best practices, you can create a secure microservices architecture using OAuth2/OIDC with JWT and Keycloak. 
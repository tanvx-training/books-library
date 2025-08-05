package com.library.member.controller.exception;

import com.library.member.business.exception.AuthenticationException;
import com.library.member.business.exception.EntityNotFoundException;
import com.library.member.business.exception.EntityServiceException;
import com.library.member.business.exception.EntityValidationException;
import com.library.member.business.exception.InvalidUuidException;
import com.library.member.business.security.UnifiedAuthenticationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Set;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final UnifiedAuthenticationService authenticationService;

    @ExceptionHandler(InvalidUuidException.class)
    public ResponseEntity<ValidationErrorResponse> handleInvalidUuid(
            InvalidUuidException ex, WebRequest request) {

        String correlationId = getCorrelationId();

        log.warn("Invalid UUID format - correlationId: {}, parameter: {}, value: {}",
                correlationId, ex.getParameterName(), ex.getInvalidValue());

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                ex.getMessage(),
                "INVALID_UUID_FORMAT",
                getPath(request),
                correlationId
        );

        // Add field-specific error if parameter name is available
        if (ex.getParameterName() != null) {
            errorResponse.addFieldError(ex.getParameterName(), ex.getMessage());
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ValidationErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String message = "Invalid parameter format";
        String parameterName = ex.getName();

        log.warn("Parameter type mismatch - correlationId: {}, parameter: {}, value: {}, requiredType: {}",
                correlationId, parameterName, ex.getValue(), ex.getRequiredType());

        // Check if it's a UUID conversion error
        if (ex.getRequiredType() != null && ex.getRequiredType().equals(java.util.UUID.class)) {
            message = String.format("Invalid UUID format for parameter '%s': %s", parameterName, ex.getValue());
        } else if (ex.getValue() != null) {
            message = String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), parameterName);
        }

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                message,
                "PARAMETER_TYPE_MISMATCH",
                getPath(request),
                correlationId
        );

        errorResponse.addFieldError(parameterName, message);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(
            EntityNotFoundException ex, WebRequest request) {

        String correlationId = getCorrelationId();

        log.warn("Entity not found - correlationId: {}, entityType: {}, entityId: {}",
                correlationId, ex.getEntityType(), ex.getEntityId());

        String errorCode = ex.getEntityType() != null ?
                ex.getEntityType().toUpperCase() + "_NOT_FOUND" : "ENTITY_NOT_FOUND";

        // Provide member-specific error codes
        if ("User".equals(ex.getEntityType())) {
            errorCode = "MEMBER_NOT_FOUND";
        } else if ("LibraryCard".equals(ex.getEntityType())) {
            errorCode = "LIBRARY_CARD_NOT_FOUND";
        }

        // Enhance error message for public_id-based lookups
        String message = ex.getMessage();
        if (message != null && message.contains("public_id")) {
            // Already contains public_id context, use as-is
        } else if (ex.getEntityId() != null) {
            // Add public_id context if not already present
            message = String.format("%s not found with public_id: %s",
                    ex.getEntityType() != null ? ex.getEntityType() : "Entity",
                    ex.getEntityId());
        }

        ErrorResponse errorResponse = new ErrorResponse(
                message != null ? message : ex.getMessage(),
                errorCode,
                getPath(request),
                correlationId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleEntityValidation(
            EntityValidationException ex, WebRequest request) {

        String correlationId = getCorrelationId();

        log.warn("Entity validation failed - correlationId: {}, entityType: {}, field: {}, value: {}, error: {}",
                correlationId, ex.getEntityType(), ex.getField(), ex.getValue(), ex.getMessage());

        String errorCode = ex.getEntityType() != null ?
                ex.getEntityType().toUpperCase() + "_VALIDATION_ERROR" : "ENTITY_VALIDATION_ERROR";

        // Determine HTTP status based on validation type
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Check for duplicate value errors (should return 409 Conflict)
        if (ex.getMessage() != null && (ex.getMessage().contains("already exists") ||
                ex.getMessage().contains("already in use") || ex.getMessage().contains("duplicate"))) {
            status = HttpStatus.CONFLICT;
            errorCode = ex.getEntityType() != null ?
                    ex.getEntityType().toUpperCase() + "_DUPLICATE_ERROR" : "DUPLICATE_ERROR";

            // Provide member-specific duplicate error codes
            if ("User".equals(ex.getEntityType())) {
                if (ex.getField() != null) {
                    errorCode = switch (ex.getField()) {
                        case "email" -> "MEMBER_EMAIL_ALREADY_EXISTS";
                        case "username" -> "MEMBER_USERNAME_ALREADY_EXISTS";
                        case "keycloakId" -> "MEMBER_KEYCLOAK_ID_ALREADY_EXISTS";
                        default -> "MEMBER_DUPLICATE_ERROR";
                    };
                }
            } else if ("LibraryCard".equals(ex.getEntityType())) {
                errorCode = "LIBRARY_CARD_NUMBER_ALREADY_EXISTS";
            }
        }

        // Check for business rule violations
        if (ex.getMessage() != null && (ex.getMessage().contains("Cannot delete") ||
                ex.getMessage().contains("has associated") || ex.getMessage().contains("dependency"))) {
            status = HttpStatus.CONFLICT;
            errorCode = ex.getEntityType() != null ?
                    ex.getEntityType().toUpperCase() + "_DEPENDENCY_ERROR" : "DEPENDENCY_ERROR";
        }

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                ex.getMessage(),
                errorCode,
                getPath(request),
                correlationId
        );

        // Add field-specific error if available
        if (ex.getField() != null) {
            errorResponse.addFieldError(ex.getField(), ex.getMessage());
        }

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        String correlationId = getCorrelationId();

        log.warn("Request validation failed - correlationId: {}, fieldErrors: {}",
                correlationId, ex.getBindingResult().getFieldErrorCount());

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                "Validation failed for one or more fields",
                "VALIDATION_ERROR",
                getPath(request),
                correlationId
        );

        // Add field-specific errors to response
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {

        String correlationId = getCorrelationId();

        log.warn("Constraint validation failed - correlationId: {}, violations: {}",
                correlationId, ex.getConstraintViolations().size());

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                "Validation failed for request parameters",
                "CONSTRAINT_VIOLATION",
                getPath(request),
                correlationId
        );

        // Add constraint violations to response
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errorResponse.addFieldError(propertyPath, message);
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityServiceException.class)
    public ResponseEntity<ErrorResponse> handleEntityService(
            EntityServiceException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();
        String requestPath = getPath(request);

        // Enhanced logging with security context
        log.error("Entity service error - correlationId: {}, userId: {}, entityType: {}, operation: {}, path: {}, error: {}",
                correlationId, currentUserId != null ? currentUserId : "UNKNOWN",
                ex.getEntityType(), ex.getOperation(), requestPath, ex.getMessage(), ex);

        String errorCode = ex.getEntityType() != null ?
                ex.getEntityType().toUpperCase() + "_SERVICE_ERROR" : "ENTITY_SERVICE_ERROR";

        // Provide member-specific service error codes
        if ("User".equals(ex.getEntityType())) {
            errorCode = "MEMBER_SERVICE_ERROR";
        } else if ("LibraryCard".equals(ex.getEntityType())) {
            errorCode = "LIBRARY_CARD_SERVICE_ERROR";
        }

        // Provide more specific error message based on operation
        String message = getMessage(ex);

        ErrorResponse errorResponse = new ErrorResponse(
                message,
                errorCode,
                requestPath,
                correlationId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static String getMessage(EntityServiceException ex) {
        String message = "An error occurred while processing the request";
        if (ex.getEntityType() != null && ex.getOperation() != null) {
            if (ex.getOperation().contains("public_id")) {
                message = String.format("An error occurred while processing the %s operation using public_id references",
                        ex.getEntityType().toLowerCase());
            } else {
                message = String.format("An error occurred while processing the %s %s operation",
                        ex.getOperation(), ex.getEntityType().toLowerCase());
            }
        } else if (ex.getEntityType() != null) {
            message = String.format("An error occurred while processing the %s operation",
                    ex.getEntityType().toLowerCase());
        }
        return message;
    }

    @ExceptionHandler(com.library.member.business.exception.AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            com.library.member.business.exception.AuthenticationException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();
        String requestPath = getPath(request);

        // Log authentication failure with security context
        log.warn("Authentication failed - correlationId: {}, userId: {}, path: {}, error: {}",
                correlationId, currentUserId != null ? currentUserId : "ANONYMOUS",
                requestPath, ex.getMessage());

        // Log additional security details for monitoring
        logSecurityEvent("AUTHENTICATION_FAILED", currentUserId, requestPath, ex.getMessage(), correlationId);

        ErrorResponse errorResponse = getErrorResponse(ex, requestPath, correlationId);

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    private static ErrorResponse getErrorResponse(AuthenticationException ex, String requestPath, String correlationId) {
        String message = "Authentication failed. Please verify your credentials and try again.";
        if (ex.getMessage() != null && !ex.getMessage().trim().isEmpty()) {
            // Provide specific error message but avoid exposing sensitive details
            if (ex.getMessage().contains("JWT token")) {
                message = "Invalid or expired authentication token. Please log in again.";
            } else if (ex.getMessage().contains("user context")) {
                message = "Authentication context is missing or invalid. Please log in again.";
            } else if (ex.getMessage().contains("claim")) {
                message = "Authentication token is missing required information. Please log in again.";
            } else {
                message = ex.getMessage();
            }
        }

        return new ErrorResponse(
                message,
                "AUTHENTICATION_FAILED",
                requestPath,
                correlationId
        );
    }

    @ExceptionHandler(com.library.member.business.exception.AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorization(
            com.library.member.business.exception.AuthorizationException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();
        String requestPath = getPath(request);

        // Log authorization failure with security context
        log.warn("Authorization failed - correlationId: {}, userId: {}, path: {}, error: {}",
                correlationId, currentUserId != null ? currentUserId : "UNKNOWN",
                requestPath, ex.getMessage());

        // Log additional security details for monitoring
        logSecurityEvent("AUTHORIZATION_FAILED", currentUserId, requestPath, ex.getMessage(), correlationId);

        String message = "Access denied. You do not have permission to perform this operation.";
        String errorCode = "AUTHORIZATION_FAILED";

        // Provide more specific error messages based on the exception details
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Insufficient permissions")) {
                message = "Insufficient permissions. You do not have the required role to perform this operation.";
                errorCode = "INSUFFICIENT_PERMISSIONS";
            } else if (ex.getMessage().contains("Access denied to")) {
                message = "Access denied. You do not have permission to access this resource.";
                errorCode = "RESOURCE_ACCESS_DENIED";
            } else if (ex.getMessage().contains("Operation not allowed")) {
                message = "Operation not allowed. This action is restricted.";
                errorCode = "OPERATION_NOT_ALLOWED";
            } else {
                // Use the original message if it doesn't expose sensitive information
                message = ex.getMessage();
            }
        }

        ErrorResponse errorResponse = new ErrorResponse(
                message,
                errorCode,
                requestPath,
                correlationId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleSpringAuthentication(
            org.springframework.security.core.AuthenticationException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();
        String requestPath = getPath(request);

        // Log Spring Security authentication failure
        log.warn("Spring Security authentication failed - correlationId: {}, userId: {}, path: {}, error: {}",
                correlationId, currentUserId != null ? currentUserId : "ANONYMOUS",
                requestPath, ex.getMessage());

        // Log security event for monitoring
        logSecurityEvent("SPRING_AUTHENTICATION_FAILED", currentUserId, requestPath, ex.getMessage(), correlationId);

        ErrorResponse errorResponse = getErrorResponse(ex, requestPath, correlationId);

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    private static ErrorResponse getErrorResponse(org.springframework.security.core.AuthenticationException ex, String requestPath, String correlationId) {
        String message = "Authentication failed. Please verify your credentials and try again.";
        String errorCode = "AUTHENTICATION_FAILED";

        // Provide specific error messages for different authentication failures
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Bad credentials")) {
                message = "Invalid credentials provided. Please check your username and password.";
                errorCode = "INVALID_CREDENTIALS";
            } else if (ex.getMessage().contains("Account is disabled")) {
                message = "Your account has been disabled. Please contact support.";
                errorCode = "ACCOUNT_DISABLED";
            } else if (ex.getMessage().contains("Account is locked")) {
                message = "Your account has been locked. Please contact support.";
                errorCode = "ACCOUNT_LOCKED";
            }
        }

        return new ErrorResponse(
                message,
                errorCode,
                requestPath,
                correlationId
        );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(
            JwtException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();
        String requestPath = getPath(request);

        // Log JWT-related errors with security context
        log.warn("JWT processing error - correlationId: {}, userId: {}, path: {}, error: {}",
                correlationId, currentUserId != null ? currentUserId : "ANONYMOUS",
                requestPath, ex.getMessage());

        // Log security event for JWT issues
        logSecurityEvent("JWT_PROCESSING_ERROR", currentUserId, requestPath, ex.getMessage(), correlationId);

        ErrorResponse errorResponse = getErrorResponse(ex, requestPath, correlationId);

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    private static ErrorResponse getErrorResponse(JwtException ex, String requestPath, String correlationId) {
        String message = "Invalid or expired authentication token. Please log in again.";
        String errorCode = "JWT_ERROR";

        // Provide specific error messages for different JWT issues
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("expired")) {
                message = "Your authentication token has expired. Please log in again.";
                errorCode = "JWT_EXPIRED";
            } else if (ex.getMessage().contains("malformed")) {
                message = "Invalid authentication token format. Please log in again.";
                errorCode = "JWT_MALFORMED";
            } else if (ex.getMessage().contains("signature")) {
                message = "Authentication token signature is invalid. Please log in again.";
                errorCode = "JWT_SIGNATURE_INVALID";
            }
        }

        return new ErrorResponse(
                message,
                errorCode,
                requestPath,
                correlationId
        );
    }

    @ExceptionHandler(InvalidBearerTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBearerToken(
            InvalidBearerTokenException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();
        String requestPath = getPath(request);

        // Log invalid bearer token with security context
        log.warn("Invalid bearer token - correlationId: {}, userId: {}, path: {}, error: {}",
                correlationId, currentUserId != null ? currentUserId : "ANONYMOUS",
                requestPath, ex.getMessage());

        // Log security event for invalid bearer tokens
        logSecurityEvent("INVALID_BEARER_TOKEN", currentUserId, requestPath, ex.getMessage(), correlationId);

        String message = "Invalid authentication token. Please log in again.";

        ErrorResponse errorResponse = new ErrorResponse(
                message,
                "INVALID_BEARER_TOKEN",
                requestPath,
                correlationId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();
        String requestPath = getPath(request);

        // Log Spring Security access denied with security context
        log.warn("Spring Security access denied - correlationId: {}, userId: {}, path: {}, error: {}",
                correlationId, currentUserId != null ? currentUserId : "UNKNOWN",
                requestPath, ex.getMessage());

        // Log additional security details for monitoring
        logSecurityEvent("SPRING_SECURITY_ACCESS_DENIED", currentUserId, requestPath, ex.getMessage(), correlationId);

        String message = "Access denied. You do not have permission to perform this operation.";
        if (ex.getMessage() != null && !ex.getMessage().trim().isEmpty()) {
            message = ex.getMessage();
        }

        ErrorResponse errorResponse = new ErrorResponse(
                message,
                "ACCESS_DENIED",
                requestPath,
                correlationId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {

        String correlationId = getCorrelationId();

        log.warn("Invalid argument - correlationId: {}, error: {}", correlationId, ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "INVALID_ARGUMENT",
                getPath(request),
                correlationId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();
        String requestPath = getPath(request);

        // Enhanced logging with security context for unexpected errors
        log.error("Unexpected error - correlationId: {}, userId: {}, path: {}, error: {}",
                correlationId, currentUserId != null ? currentUserId : "UNKNOWN",
                requestPath, ex.getMessage(), ex);

        // Log security event for unexpected errors that might indicate security issues
        if (isSecurityRelated(ex)) {
            logSecurityEvent("UNEXPECTED_SECURITY_ERROR", currentUserId, requestPath,
                    ex.getMessage(), correlationId);
        }

        ErrorResponse errorResponse = new ErrorResponse(
                "An unexpected error occurred. Please try again later.",
                "INTERNAL_SERVER_ERROR",
                requestPath,
                correlationId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getPath(WebRequest request) {
        String description = request.getDescription(false);
        if (description.startsWith("uri=")) {
            return description.substring(4);
        }
        return null;
    }

    private String getCorrelationId() {
        return UUID.randomUUID().toString();
    }

    private void logSecurityEvent(String eventType, String userId, String requestPath,
                                  String errorMessage, String correlationId) {
        try {
            // Create structured log entry for security monitoring
            log.info("SECURITY_EVENT - type: {}, userId: {}, path: {}, correlationId: {}, message: {}",
                    eventType,
                    userId != null ? userId : "ANONYMOUS",
                    requestPath != null ? requestPath : "UNKNOWN",
                    correlationId,
                    errorMessage != null ? errorMessage : "No additional details");

            // Additional detailed logging for security analysis (DEBUG level)
            if (log.isDebugEnabled()) {
                log.debug("Security event details - eventType: {}, userId: {}, requestPath: {}, " +
                                "correlationId: {}, timestamp: {}, errorMessage: {}",
                        eventType, userId, requestPath, correlationId,
                        java.time.LocalDateTime.now(), errorMessage);
            }
        } catch (Exception e) {
            // Ensure logging failures don't affect the main exception handling flow
            log.error("Failed to log security event - eventType: {}, correlationId: {}, error: {}",
                    eventType, correlationId, e.getMessage());
        }
    }

    private boolean isSecurityRelated(Exception ex) {
        if (ex == null) {
            return false;
        }

        String exceptionName = ex.getClass().getSimpleName().toLowerCase();
        String message = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        // Check for security-related exception types
        if (exceptionName.contains("security") ||
                exceptionName.contains("authentication") ||
                exceptionName.contains("authorization") ||
                exceptionName.contains("access") ||
                exceptionName.contains("jwt") ||
                exceptionName.contains("token")) {
            return true;
        }

        // Check for security-related error messages
        return message.contains("authentication") ||
                message.contains("authorization") ||
                message.contains("access denied") ||
                message.contains("permission") ||
                message.contains("jwt") ||
                message.contains("token") ||
                message.contains("unauthorized") ||
                message.contains("forbidden") ||
                message.contains("security");
    }
}
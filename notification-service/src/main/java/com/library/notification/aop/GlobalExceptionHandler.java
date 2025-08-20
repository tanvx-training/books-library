package com.library.notification.aop;

import com.library.notification.service.UnifiedAuthenticationService;
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

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotificationNotFound(
            NotificationNotFoundException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();

        log.warn("Notification not found - correlationId: {}, userId: {}, error: {}",
                correlationId, currentUserId, ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "NOTIFICATION_NOT_FOUND",
                getPath(request),
                correlationId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotificationAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleNotificationAccessDenied(
            NotificationAccessDeniedException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();

        log.warn("Notification access denied - correlationId: {}, userId: {}, error: {}",
                correlationId, currentUserId, ex.getMessage());

        logSecurityEvent("NOTIFICATION_ACCESS_DENIED", currentUserId, getPath(request), ex.getMessage(), correlationId);

        ErrorResponse errorResponse = new ErrorResponse(
                "Access denied. You do not have permission to access this notification.",
                "NOTIFICATION_ACCESS_DENIED",
                getPath(request),
                correlationId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidNotificationStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidNotificationStatus(
            InvalidNotificationStatusException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();

        log.warn("Invalid notification status - correlationId: {}, userId: {}, error: {}",
                correlationId, currentUserId, ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "INVALID_NOTIFICATION_STATUS",
                getPath(request),
                correlationId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotificationDeliveryException.class)
    public ResponseEntity<ErrorResponse> handleNotificationDelivery(
            NotificationDeliveryException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();

        log.error("Notification delivery failed - correlationId: {}, userId: {}, error: {}",
                correlationId, currentUserId, ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                "Failed to deliver notification. Please try again later.",
                "NOTIFICATION_DELIVERY_FAILED",
                getPath(request),
                correlationId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotificationPreferencesNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotificationPreferencesNotFound(
            NotificationPreferencesNotFoundException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();

        log.warn("Notification preferences not found - correlationId: {}, userId: {}, error: {}",
                correlationId, currentUserId, ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "NOTIFICATION_PREFERENCES_NOT_FOUND",
                getPath(request),
                correlationId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
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

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errorResponse.addFieldError(propertyPath, message);
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

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleSpringAuthentication(
            org.springframework.security.core.AuthenticationException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();

        log.warn("Spring Security authentication failed - correlationId: {}, userId: {}, error: {}",
                correlationId, currentUserId != null ? currentUserId : "ANONYMOUS", ex.getMessage());

        logSecurityEvent("SPRING_AUTHENTICATION_FAILED", currentUserId, getPath(request), ex.getMessage(), correlationId);

        ErrorResponse errorResponse = new ErrorResponse(
                "Authentication failed. Please verify your credentials and try again.",
                "AUTHENTICATION_FAILED",
                getPath(request),
                correlationId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(
            JwtException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();

        log.warn("JWT processing error - correlationId: {}, userId: {}, error: {}",
                correlationId, currentUserId != null ? currentUserId : "ANONYMOUS", ex.getMessage());

        logSecurityEvent("JWT_PROCESSING_ERROR", currentUserId, getPath(request), ex.getMessage(), correlationId);

        String message = "Invalid or expired authentication token. Please log in again.";
        String errorCode = "JWT_ERROR";

        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("expired")) {
                message = "Your authentication token has expired. Please log in again.";
                errorCode = "JWT_EXPIRED";
            } else if (ex.getMessage().contains("malformed")) {
                message = "Invalid authentication token format. Please log in again.";
                errorCode = "JWT_MALFORMED";
            }
        }

        ErrorResponse errorResponse = new ErrorResponse(
                message,
                errorCode,
                getPath(request),
                correlationId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidBearerTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBearerToken(
            InvalidBearerTokenException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();

        log.warn("Invalid bearer token - correlationId: {}, userId: {}, error: {}",
                correlationId, currentUserId != null ? currentUserId : "ANONYMOUS", ex.getMessage());

        logSecurityEvent("INVALID_BEARER_TOKEN", currentUserId, getPath(request), ex.getMessage(), correlationId);

        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid authentication token. Please log in again.",
                "INVALID_BEARER_TOKEN",
                getPath(request),
                correlationId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {

        String correlationId = getCorrelationId();
        String currentUserId = authenticationService.getCurrentUserKeycloakId();

        log.warn("Spring Security access denied - correlationId: {}, userId: {}, error: {}",
                correlationId, currentUserId != null ? currentUserId : "UNKNOWN", ex.getMessage());

        logSecurityEvent("SPRING_SECURITY_ACCESS_DENIED", currentUserId, getPath(request), ex.getMessage(), correlationId);

        ErrorResponse errorResponse = new ErrorResponse(
                "Access denied. You do not have permission to perform this operation.",
                "ACCESS_DENIED",
                getPath(request),
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

        log.error("Unexpected error - correlationId: {}, userId: {}, error: {}",
                correlationId, currentUserId != null ? currentUserId : "UNKNOWN", ex.getMessage(), ex);

        if (isSecurityRelated(ex)) {
            logSecurityEvent("UNEXPECTED_SECURITY_ERROR", currentUserId, getPath(request),
                    ex.getMessage(), correlationId);
        }

        ErrorResponse errorResponse = new ErrorResponse(
                "An unexpected error occurred. Please try again later.",
                "INTERNAL_SERVER_ERROR",
                getPath(request),
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
            log.info("SECURITY_EVENT - type: {}, userId: {}, path: {}, correlationId: {}, message: {}",
                    eventType,
                    userId != null ? userId : "ANONYMOUS",
                    requestPath != null ? requestPath : "UNKNOWN",
                    correlationId,
                    errorMessage != null ? errorMessage : "No additional details");
        } catch (Exception e) {
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

        return exceptionName.contains("security") ||
                exceptionName.contains("authentication") ||
                exceptionName.contains("authorization") ||
                exceptionName.contains("access") ||
                exceptionName.contains("jwt") ||
                exceptionName.contains("token") ||
                message.contains("authentication") ||
                message.contains("authorization") ||
                message.contains("access denied") ||
                message.contains("permission") ||
                message.contains("jwt") ||
                message.contains("token");
    }
}
package com.library.catalog.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;

/**
 * Simplified Global exception handler using filter-based logging.
 * 
 * This version demonstrates how exception handling becomes cleaner when
 * logging concerns are handled by filters and aspects:
 * - No manual logging context management
 * - No repetitive logging code
 * - Focus on exception handling and response formatting only
 * - Automatic error logging via ControllerLoggingAspect
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles AuthorNotFoundException and returns HTTP 404.
     * All error logging is handled automatically by ControllerLoggingAspect.
     */
    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthorNotFound(
            AuthorNotFoundException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "AUTHOR_NOT_FOUND",
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles AuthorValidationException and returns HTTP 400.
     * All error logging is handled automatically.
     */
    @ExceptionHandler(AuthorValidationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorValidation(
            AuthorValidationException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "AUTHOR_VALIDATION_ERROR",
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MethodArgumentNotValidException (Bean Validation) and returns HTTP 400.
     * All error logging is handled automatically.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
            "Validation failed for one or more fields",
            "VALIDATION_ERROR",
            getPath(request)
        );
        
        // Add field-specific errors to response
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ConstraintViolationException (method parameter validation) and returns HTTP 400.
     * All error logging is handled automatically.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
            "Validation failed for request parameters",
            "CONSTRAINT_VIOLATION",
            getPath(request)
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

    /**
     * Handles AuthorServiceException and returns HTTP 500.
     * All error logging is handled automatically.
     */
    @ExceptionHandler(AuthorServiceException.class)
    public ResponseEntity<ErrorResponse> handleAuthorService(
            AuthorServiceException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "An error occurred while processing the author operation",
            "AUTHOR_SERVICE_ERROR",
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles all other unexpected exceptions and returns HTTP 500.
     * All error logging is handled automatically.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "An unexpected error occurred. Please try again later.",
            "INTERNAL_SERVER_ERROR",
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Extracts the request path from WebRequest.
     */
    private String getPath(WebRequest request) {
        String description = request.getDescription(false);
        if (description.startsWith("uri=")) {
            return description.substring(4);
        }
        return null;
    }
}
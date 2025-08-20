package com.library.catalog.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateCopyNumberException.class)
    public ResponseEntity<ValidationErrorResponse> handleDuplicateCopyNumber(
            DuplicateCopyNumberException ex, WebRequest request) {
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
            ex.getMessage(),
            "DUPLICATE_COPY_NUMBER",
            getPath(request)
        );
        
        // Add field-specific error for copy number
        errorResponse.addFieldError("copyNumber", 
            String.format("Copy number '%s' already exists for this book", ex.getCopyNumber()));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ValidationErrorResponse> handleInvalidStatusTransition(
            InvalidStatusTransitionException ex, WebRequest request) {
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
            ex.getMessage(),
            "INVALID_STATUS_TRANSITION",
            getPath(request)
        );
        
        // Add field-specific error for status
        if (ex.getFromStatus() != null && ex.getToStatus() != null) {
            errorResponse.addFieldError("status", 
                String.format("Cannot change status from %s to %s", ex.getFromStatus(), ex.getToStatus()));
        } else if (ex.getFromStatus() != null) {
            errorResponse.addFieldError("status", 
                String.format("Current status %s does not allow this operation", ex.getFromStatus()));
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUuidException.class)
    public ResponseEntity<ValidationErrorResponse> handleInvalidUuid(
            InvalidUuidException ex, WebRequest request) {
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
            ex.getMessage(),
            "INVALID_UUID_FORMAT",
            getPath(request)
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
        
        String message = "Invalid parameter format";
        String parameterName = ex.getName();
        
        // Check if it's a UUID conversion error
        if (ex.getRequiredType() != null && ex.getRequiredType().equals(java.util.UUID.class)) {
            message = String.format("Invalid UUID format for parameter '%s': %s", parameterName, ex.getValue());
        } else if (ex.getValue() != null) {
            message = String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), parameterName);
        }
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
            message,
            "PARAMETER_TYPE_MISMATCH",
            getPath(request)
        );
        
        errorResponse.addFieldError(parameterName, message);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(
            EntityNotFoundException ex, WebRequest request) {
        
        String errorCode = ex.getEntityType() != null ? 
            ex.getEntityType().toUpperCase() + "_NOT_FOUND" : "ENTITY_NOT_FOUND";
        
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
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleEntityValidation(
            EntityValidationException ex, WebRequest request) {
        
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
            getPath(request)
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

    @ExceptionHandler(EntityServiceException.class)
    public ResponseEntity<ErrorResponse> handleEntityService(
            EntityServiceException ex, WebRequest request) {
        
        String errorCode = ex.getEntityType() != null ? 
            ex.getEntityType().toUpperCase() + "_SERVICE_ERROR" : "ENTITY_SERVICE_ERROR";
        
        // Provide more specific error message based on operation
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
        
        ErrorResponse errorResponse = new ErrorResponse(
            message,
            errorCode,
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "INVALID_ARGUMENT",
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
            "An unexpected error occurred. Please try again later.",
            "INTERNAL_SERVER_ERROR",
            getPath(request)
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
}
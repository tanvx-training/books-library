package com.library.book.interfaces.rest.exception;

import com.library.book.application.exception.AuthorApplicationException;
import com.library.book.application.exception.AuthorNotFoundException;
import com.library.book.domain.exception.AuthorDomainException;
import com.library.book.domain.exception.InvalidAuthorDataException;
import com.library.book.infrastructure.exception.AuthorPersistenceException;
import com.library.common.dto.ApiError;
import com.library.common.dto.ApiResponse;
import com.library.common.dto.ApiValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

/**
 * Exception handler specifically for Author-related exceptions.
 * Uses a higher order than the global exception handler to ensure it handles author exceptions first.
 */
@RestControllerAdvice(basePackages = "com.library.book.interfaces.rest")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class AuthorExceptionHandler {

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthorNotFound(AuthorNotFoundException ex) {
        log.warn("Author not found: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(InvalidAuthorDataException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidAuthorData(InvalidAuthorDataException ex) {
        log.warn("Invalid author data: {}", ex.getMessage());
        ApiValidationError validationError = new ApiValidationError(
                ex.getField(),
                ex.getReason()
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid author data",
                Collections.singletonList(validationError)
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(AuthorDomainException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthorDomainException(AuthorDomainException ex) {
        log.warn("Author domain exception: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(AuthorApplicationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthorApplicationException(AuthorApplicationException ex) {
        log.error("Author application exception", ex);
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An error occurred while processing author data",
                null
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(AuthorPersistenceException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthorPersistenceException(AuthorPersistenceException ex) {
        log.error("Author persistence exception", ex);
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An error occurred while saving author data",
                null
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }
}
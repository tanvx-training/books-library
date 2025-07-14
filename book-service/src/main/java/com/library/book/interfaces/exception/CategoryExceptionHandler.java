package com.library.book.interfaces.exception;

import com.library.book.application.exception.CategoryApplicationException;
import com.library.book.application.exception.CategoryNotFoundException;
import com.library.book.domain.exception.CategoryDomainException;
import com.library.book.domain.exception.InvalidCategoryDataException;
import com.library.book.infrastructure.exception.CategoryPersistenceException;
import com.library.book.application.dto.response.ApiError;
import com.library.book.application.dto.response.ApiResponse;
import com.library.book.application.dto.response.ApiValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

/**
 * Exception handler specifically for Category-related exceptions.
 * Uses a higher order than the global exception handler to ensure it handles category exceptions first.
 */
@RestControllerAdvice(basePackages = "com.library.book.interfaces.rest")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CategoryExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleCategoryNotFound(CategoryNotFoundException ex) {
        log.warn("Category not found: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );

        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(InvalidCategoryDataException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCategoryData(InvalidCategoryDataException ex) {
        log.warn("Invalid category data: {}", ex.getMessage());

        ApiValidationError validationError = new ApiValidationError(
                ex.getField(),
                ex.getReason()
        );

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid category data",
                Collections.singletonList(validationError)
        );

        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(CategoryDomainException.class)
    public ResponseEntity<ApiResponse<Object>> handleCategoryDomainException(CategoryDomainException ex) {
        log.warn("Category domain exception: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null
        );

        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(CategoryApplicationException.class)
    public ResponseEntity<ApiResponse<Object>> handleCategoryApplicationException(CategoryApplicationException ex) {
        log.error("Category application exception", ex);

        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An error occurred while processing category data",
                null
        );

        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(CategoryPersistenceException.class)
    public ResponseEntity<ApiResponse<Object>> handleCategoryPersistenceException(CategoryPersistenceException ex) {
        log.error("Category persistence exception", ex);

        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An error occurred while saving category data",
                null
        );

        return ResponseEntity.ok(ApiResponse.error(apiError));
    }
}
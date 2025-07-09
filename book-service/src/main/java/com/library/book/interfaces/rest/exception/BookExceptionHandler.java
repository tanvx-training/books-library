package com.library.book.interfaces.rest.exception;

import com.library.book.application.exception.BookApplicationException;
import com.library.book.application.exception.BookNotFoundException;
import com.library.book.domain.exception.BookDomainException;
import com.library.book.domain.exception.InvalidBookDataException;
import com.library.book.infrastructure.exception.BookPersistenceException;
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
 * Exception handler specifically for Book-related exceptions.
 * Uses a higher order than the global exception handler to ensure it handles book exceptions first.
 */
@RestControllerAdvice(basePackages = "com.library.book.interfaces.rest")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class BookExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleBookNotFound(BookNotFoundException ex) {
        log.warn("Book not found: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(InvalidBookDataException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidBookData(InvalidBookDataException ex) {
        log.warn("Invalid book data: {}", ex.getMessage());
        ApiValidationError validationError = new ApiValidationError(
                ex.getField(),
                ex.getReason()
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid book data",
                Collections.singletonList(validationError)
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(BookDomainException.class)
    public ResponseEntity<ApiResponse<Object>> handleBookDomainException(BookDomainException ex) {
        log.warn("Book domain exception: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(BookApplicationException.class)
    public ResponseEntity<ApiResponse<Object>> handleBookApplicationException(BookApplicationException ex) {
        log.error("Book application exception", ex);
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An error occurred while processing book data",
                null
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(BookPersistenceException.class)
    public ResponseEntity<ApiResponse<Object>> handleBookPersistenceException(BookPersistenceException ex) {
        log.error("Book persistence exception", ex);
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An error occurred while saving book data",
                null
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }
} 
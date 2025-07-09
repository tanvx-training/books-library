package com.library.book.interfaces.rest.exception;

import com.library.book.application.dto.response.ApiError;
import com.library.book.application.dto.response.ApiResponse;
import com.library.book.application.dto.response.ApiValidationError;
import com.library.book.application.exception.BookCopyApplicationException;
import com.library.book.domain.exception.BookCopyNotFoundException;
import com.library.book.domain.exception.InvalidBookCopyDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BookCopyExceptionHandler {

    @ExceptionHandler(BookCopyNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleBookCopyNotFoundException(BookCopyNotFoundException ex) {
        log.error("Book copy not found: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }
    
    @ExceptionHandler(InvalidBookCopyDataException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidBookCopyDataException(InvalidBookCopyDataException ex) {
        log.warn("Invalid book copy data: {}", ex.getMessage());
        ApiValidationError validationError = new ApiValidationError(
                ex.getField(),
                ex.getMessage()
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid book data",
                Collections.singletonList(validationError)
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }
    
    @ExceptionHandler(BookCopyApplicationException.class)
    public ResponseEntity<ApiResponse<Object>> handleBookCopyApplicationException(BookCopyApplicationException ex) {
        log.error("Book copy application exception", ex);
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An error occurred while processing book copy data",
                null
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }
} 
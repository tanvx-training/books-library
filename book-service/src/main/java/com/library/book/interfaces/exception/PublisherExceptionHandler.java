package com.library.book.interfaces.exception;

import com.library.book.application.dto.response.ApiError;
import com.library.book.application.dto.response.ApiResponse;
import com.library.book.application.dto.response.ApiValidationError;
import com.library.book.application.exception.PublisherApplicationException;
import com.library.book.application.exception.PublisherNotFoundException;
import com.library.book.domain.exception.InvalidPublisherDataException;
import com.library.book.domain.exception.PublisherDomainException;
import com.library.book.infrastructure.exception.PublisherPersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@RestControllerAdvice(basePackages = "com.library.book.interfaces.rest")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class PublisherExceptionHandler {

    @ExceptionHandler(PublisherNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handlePublisherNotFound(PublisherNotFoundException ex) {
        log.warn("Publisher not found: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );

        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(InvalidPublisherDataException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidPublisherData(InvalidPublisherDataException ex) {
        log.warn("Invalid publisher data: {}", ex.getMessage());

        ApiValidationError validationError = new ApiValidationError(
                ex.getField(),
                ex.getReason()
        );

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid publisher data",
                Collections.singletonList(validationError)
        );

        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(PublisherDomainException.class)
    public ResponseEntity<ApiResponse<Object>> handlePublisherDomainException(PublisherDomainException ex) {
        log.warn("Publisher domain exception: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null
        );

        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(PublisherApplicationException.class)
    public ResponseEntity<ApiResponse<Object>> handlePublisherApplicationException(PublisherApplicationException ex) {
        log.error("Publisher application exception: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                null
        );

        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(PublisherPersistenceException.class)
    public ResponseEntity<ApiResponse<Object>> handlePublisherPersistenceException(PublisherPersistenceException ex) {
        log.error("Publisher persistence exception: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An error occurred while saving publisher data",
                null
        );

        return ResponseEntity.ok(ApiResponse.error(apiError));
    }
}
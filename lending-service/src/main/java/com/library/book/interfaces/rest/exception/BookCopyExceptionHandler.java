package com.library.book.interfaces.rest.exception;

import com.library.book.application.exception.BookCopyApplicationException;
import com.library.book.domain.exception.BookCopyNotFoundException;
import com.library.book.domain.exception.InvalidBookCopyDataException;
import com.library.common.dto.ApiError;
import com.library.common.dto.ApiResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BookCopyExceptionHandler {

    @ExceptionHandler(BookCopyNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleBookCopyNotFoundException(BookCopyNotFoundException ex) {
        ApiError error = ApiError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(error));
    }
    
    @ExceptionHandler(InvalidBookCopyDataException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidBookCopyDataException(InvalidBookCopyDataException ex) {
        ApiError error = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .field(ex.getField())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(error));
    }
    
    @ExceptionHandler(BookCopyApplicationException.class)
    public ResponseEntity<ApiResponse<Object>> handleBookCopyApplicationException(BookCopyApplicationException ex) {
        ApiError error = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(error));
    }
} 
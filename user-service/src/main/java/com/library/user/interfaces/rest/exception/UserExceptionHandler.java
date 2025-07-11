package com.library.user.interfaces.rest.exception;

import com.library.user.application.dto.response.ApiError;
import com.library.user.application.dto.response.ApiResponse;
import com.library.user.application.dto.response.ApiValidationError;
import com.library.user.application.exception.UserApplicationException;
import com.library.user.domain.exception.DomainException;
import com.library.user.domain.exception.InvalidUserDataException;
import com.library.user.domain.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@RestControllerAdvice(basePackages = "com.library.user.interfaces.rest")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFound(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidUserData(InvalidUserDataException ex) {
        log.warn("Invalid user data: {}", ex.getMessage());
        ApiValidationError validationError = new ApiValidationError(
                ex.getField(),
                ex.getReason()
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid user data",
                Collections.singletonList(validationError)
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Object>> handleDomainException(DomainException ex) {
        log.warn("Domain exception: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(UserApplicationException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserApplicationException(UserApplicationException ex) {
        log.warn("User application exception: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                null
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error in user service", ex);
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                null
        );
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }
}
package com.library.common.aop.exception;

import com.library.common.dto.ApiError;
import com.library.common.dto.ApiResponse;
import com.library.common.dto.ApiValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Xử lý các lỗi validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        List<ApiValidationError> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiValidationError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Validation failed", validationErrors);

        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    // Xử lý các lỗi nghiệp vụ (ví dụ: không tìm thấy tài nguyên)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }

    // Xử lý các lỗi chung khác
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex, WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred", null);
        // Nên log lỗi chi tiết ở đây
        // log.error("Unhandled exception:", ex);
        return ResponseEntity.ok(ApiResponse.error(apiError));
    }
}

package com.library.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // Không serialize các trường có giá trị null
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;
    private ApiError error;

    // Constructor cho các trường hợp private để ép buộc sử dụng static factory methods
    private ApiResponse(HttpStatus status, String message, T data) {
        this.status = status.value();
        this.message = message;
        this.data = data;
    }

    private ApiResponse(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }

    private ApiResponse(ApiError error) {
        this.status = error.getStatus();
        this.message = error.getMessage();
        this.error = error;
    }


    // --- Các Factory Method cho Success Response ---

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK, "Success", data);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return new ApiResponse<>(status, status.getReasonPhrase(), data);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) {
        return new ApiResponse<>(status, message, data);
    }

    public static ApiResponse<Void> success(HttpStatus status, String message) {
        return new ApiResponse<>(status, message);
    }


    // --- Các Factory Method cho Error Response ---

    public static <T> ApiResponse<T> error(ApiError apiError) {
        return new ApiResponse<>(apiError);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(new ApiError(status.value(), message, null));
    }
}
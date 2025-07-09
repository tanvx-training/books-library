package com.library.book.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private int status;
    private String message;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private List<ApiValidationError> validationErrors;

    public ApiError(int status, String message, List<ApiValidationError> validationErrors) {
        this.status = status;
        this.message = message;
        this.validationErrors = validationErrors;
    }
}


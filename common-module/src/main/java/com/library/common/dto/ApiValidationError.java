package com.library.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiValidationError {
    private String field;
    private String message;

    public ApiValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }
}

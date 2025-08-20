package com.library.member.aop;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class ValidationErrorResponse extends ErrorResponse {

    private Map<String, String> fieldErrors;

    public ValidationErrorResponse() {
        super();
        this.fieldErrors = new HashMap<>();
    }

    public ValidationErrorResponse(String message) {
        super(message);
        this.fieldErrors = new HashMap<>();
    }

    public ValidationErrorResponse(String message, String code) {
        super(message, code);
        this.fieldErrors = new HashMap<>();
    }

    public ValidationErrorResponse(String message, String code, String path) {
        super(message, code, path);
        this.fieldErrors = new HashMap<>();
    }

    public ValidationErrorResponse(String message, String code, String path, String correlationId) {
        super(message, code, path, correlationId);
        this.fieldErrors = new HashMap<>();
    }

    public void addFieldError(String field, String error) {
        this.fieldErrors.put(field, error);
    }
}
package com.library.notification.aop;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ErrorResponse {

    private List<FieldError> fieldErrors;

    public ValidationErrorResponse(String message, String errorCode, String path, String correlationId) {
        super(message, errorCode, path, correlationId);
        this.fieldErrors = new ArrayList<>();
    }

    public void addFieldError(String field, String message) {
        fieldErrors.add(new FieldError(field, message));
    }

    @Data
    public static class FieldError {
        private String field;
        private String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
package com.library.catalog.controller.exception;

import com.library.member.controller.exception.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Error response DTO for validation errors.
 * Extends ErrorResponse to include field-specific validation errors.
 */
public class ValidationErrorResponse extends ErrorResponse {
    
    private Map<String, String> fieldErrors;

    /**
     * Default constructor.
     */
    public ValidationErrorResponse() {
        super();
        this.fieldErrors = new HashMap<>();
    }

    /**
     * Constructor with message.
     *
     * @param message the error message
     */
    public ValidationErrorResponse(String message) {
        super(message);
        this.fieldErrors = new HashMap<>();
    }

    /**
     * Constructor with message and code.
     *
     * @param message the error message
     * @param code the error code
     */
    public ValidationErrorResponse(String message, String code) {
        super(message, code);
        this.fieldErrors = new HashMap<>();
    }

    /**
     * Constructor with message, code, and path.
     *
     * @param message the error message
     * @param code the error code
     * @param path the request path where the error occurred
     */
    public ValidationErrorResponse(String message, String code, String path) {
        super(message, code, path);
        this.fieldErrors = new HashMap<>();
    }

    /**
     * Adds a field error to the response.
     *
     * @param field the field name that failed validation
     * @param error the validation error message for the field
     */
    public void addFieldError(String field, String error) {
        this.fieldErrors.put(field, error);
    }

    /**
     * Gets the field errors map.
     *
     * @return map of field names to error messages
     */
    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    /**
     * Sets the field errors map.
     *
     * @param fieldErrors map of field names to error messages
     */
    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
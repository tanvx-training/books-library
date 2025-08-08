package com.library.loan.business.exception;

import lombok.Getter;

@Getter
public class EntityValidationException extends RuntimeException {

    private final String entityType;

    private final String field;

    private final Object value;

    public EntityValidationException(String message) {
        super(message);
        this.entityType = null;
        this.field = null;
        this.value = null;
    }

    public EntityValidationException(String message, Throwable cause) {
        super(message, cause);
        this.entityType = null;
        this.field = null;
        this.value = null;
    }

    public EntityValidationException(String entityType, String field, Object value, String message) {
        super(message);
        this.entityType = entityType;
        this.field = field;
        this.value = value;
    }

    public static EntityValidationException duplicateValue(String entityType, String field, Object value) {
        String message = String.format("%s with %s '%s' already exists", entityType, field, value);
        return new EntityValidationException(entityType, field, value, message);
    }

    public static EntityValidationException duplicateValue(String entityType, String field, Object value, String customMessage) {
        return new EntityValidationException(entityType, field, value, customMessage);
    }

    public static EntityValidationException invalidField(String entityType, String field, Object value, String reason) {
        String message = String.format("Invalid %s %s '%s': %s", entityType, field, value, reason);
        return new EntityValidationException(entityType, field, value, message);
    }

    public static EntityValidationException requiredField(String entityType, String field) {
        String message = String.format("%s %s is required", entityType, field);
        return new EntityValidationException(entityType, field, null, message);
    }

    public static EntityValidationException businessRule(String entityType, String rule) {
        String message = String.format("%s validation failed: %s", entityType, rule);
        return new EntityValidationException(message);
    }

    public static EntityValidationException constraintViolation(String entityType, String constraint, String details) {
        String message = String.format("%s constraint violation (%s): %s", entityType, constraint, details);
        return new EntityValidationException(message);
    }

}
package com.library.common.exception;

import lombok.Getter;

@Getter
public class ResourceExistedException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceExistedException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s existed with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
} 
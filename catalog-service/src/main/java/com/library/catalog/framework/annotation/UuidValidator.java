package com.library.catalog.framework.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;

public class UuidValidator implements ConstraintValidator<ValidUuid, String> {
    
    private boolean allowNull;
    
    @Override
    public void initialize(ValidUuid constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return allowNull;
        }
        
        if (value.trim().isEmpty()) {
            return false;
        }
        
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
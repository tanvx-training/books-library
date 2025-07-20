package com.library.catalog.business.util;

import com.library.catalog.business.aop.exception.EntityNotFoundException;
import com.library.catalog.business.aop.exception.EntityServiceException;
import com.library.catalog.business.aop.exception.EntityValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityExceptionUtils {

    public static void requireValid(boolean condition, String entityType, String message) {
        if (!condition) {
            throw new EntityValidationException(entityType, null, null, message);
        }
    }

    public static void requireValidField(boolean condition, String entityType, String field, Object value, String reason) {
        if (!condition) {
            throw EntityValidationException.invalidField(entityType, field, value, reason);
        }
    }
    public static <T> void requireNonNull(T value, String entityType, String field) {
        if (value == null) {
            throw EntityValidationException.requiredField(entityType, field);
        }
    }

    public static String requireNonEmpty(String value, String entityType, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw EntityValidationException.requiredField(entityType, field);
        }
        return value.trim();
    }

    public static <T> T wrapServiceOperation(String entityType, String operation, Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (RuntimeException e) {
            throw e; // Re-throw runtime exceptions as-is
        } catch (Exception e) {
            throw EntityServiceException.databaseError(entityType, operation, e);
        }
    }

    public static void requireNoDuplicate(boolean isDuplicate, String entityType, String field, Object value) {
        if (isDuplicate) {
            throw EntityValidationException.duplicateValue(entityType, field, value);
        }
    }

    public static <T> T requireNotDeleted(T entity, String entityType, Object entityId, java.util.function.Function<T, Boolean> isDeletedFunction) {
        if (entity != null && isDeletedFunction.apply(entity)) {
            throw EntityNotFoundException.forEntity(entityType, entityId);
        }
        return entity;
    }
}
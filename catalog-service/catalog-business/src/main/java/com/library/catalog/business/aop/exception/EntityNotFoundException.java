package com.library.catalog.business.aop.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    private final String entityType;

    private final Object entityId;

    public EntityNotFoundException(String message) {
        super(message);
        this.entityType = null;
        this.entityId = null;
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.entityType = null;
        this.entityId = null;
    }

    public EntityNotFoundException(String entityType, Object entityId) {
        super(String.format("%s not found with ID: %s", entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public EntityNotFoundException(String entityType, Object entityId, String message) {
        super(message);
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public static EntityNotFoundException forEntity(String entityType, Object entityId) {
        return new EntityNotFoundException(entityType, entityId);
    }

    public static EntityNotFoundException forCriteria(String entityType, String criteria) {
        return new EntityNotFoundException(String.format("%s not found with criteria: %s", entityType, criteria));
    }

}
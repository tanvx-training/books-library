package com.library.member.aop;

import lombok.Getter;

@Getter
public class EntityServiceException extends RuntimeException {

    private final String entityType;

    private final String operation;

    public EntityServiceException(String message) {
        super(message);
        this.entityType = null;
        this.operation = null;
    }

    public EntityServiceException(String message, Throwable cause) {
        super(message, cause);
        this.entityType = null;
        this.operation = null;
    }

    public EntityServiceException(String entityType, String operation, String message) {
        super(message);
        this.entityType = entityType;
        this.operation = operation;
    }

    public EntityServiceException(String entityType, String operation, String message, Throwable cause) {
        super(message, cause);
        this.entityType = entityType;
        this.operation = operation;
    }

    public static EntityServiceException databaseError(String entityType, String operation, Throwable cause) {
        String message = String.format("Failed to %s %s due to database error", operation, entityType);
        return new EntityServiceException(entityType, operation, message, cause);
    }

    public static EntityServiceException mappingError(String entityType, Throwable cause) {
        String message = String.format("Failed to map %s data", entityType);
        return new EntityServiceException(entityType, "mapping", message, cause);
    }

    public static EntityServiceException externalServiceError(String entityType, String operation, 
                                                             String serviceName, Throwable cause) {
        String message = String.format("Failed to %s %s due to %s service error", operation, entityType, serviceName);
        return new EntityServiceException(entityType, operation, message, cause);
    }

    public static EntityServiceException concurrencyConflict(String entityType, String operation, Object entityId) {
        String message = String.format("Concurrency conflict while trying to %s %s with ID: %s", 
                                      operation, entityType, entityId);
        return new EntityServiceException(entityType, operation, message);
    }

    public static EntityServiceException businessLogicError(String entityType, String operation, String businessRule) {
        String message = String.format("Cannot %s %s: %s", operation, entityType, businessRule);
        return new EntityServiceException(entityType, operation, message);
    }

}
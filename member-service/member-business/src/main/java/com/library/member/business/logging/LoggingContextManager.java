package com.library.member.business.logging;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * Utility class for managing logging context with MDC (Mapped Diagnostic Context).
 * Provides methods to set and clear context information for structured logging.
 */
public class LoggingContextManager {

    // MDC Keys
    public static final String USER_ID = "userId";
    public static final String REQUEST_ID = "requestId";
    public static final String OPERATION = "operation";
    public static final String ENTITY_TYPE = "entityType";
    public static final String ENTITY_ID = "entityId";

    /**
     * Sets the user context in MDC.
     *
     * @param userId the current user ID
     */
    public static void setUserContext(String userId) {
        if (userId != null && !userId.trim().isEmpty()) {
            MDC.put(USER_ID, userId);
        }
    }

    /**
     * Sets the request context in MDC.
     *
     * @param requestId the request ID (generated if null)
     */
    public static void setRequestContext(String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        MDC.put(REQUEST_ID, requestId);
    }

    /**
     * Sets the operation context in MDC.
     *
     * @param operation the operation being performed (e.g., CREATE, READ, UPDATE, DELETE)
     */
    public static void setOperationContext(String operation) {
        if (operation != null && !operation.trim().isEmpty()) {
            MDC.put(OPERATION, operation);
        }
    }

    /**
     * Sets the entity context in MDC.
     *
     * @param entityType the type of entity (e.g., AUTHOR)
     * @param entityId the entity ID
     */
    public static void setEntityContext(String entityType, Object entityId) {
        if (entityType != null && !entityType.trim().isEmpty()) {
            MDC.put(ENTITY_TYPE, entityType);
        }
        if (entityId != null) {
            MDC.put(ENTITY_ID, entityId.toString());
        }
    }

    /**
     * Sets complete operation context for CRUD operations.
     *
     * @param userId the current user ID
     * @param operation the operation being performed
     * @param entityType the type of entity
     * @param entityId the entity ID (can be null for CREATE operations)
     */
    public static void setOperationContext(String userId, String operation, String entityType, Object entityId) {
        setUserContext(userId);
        setOperationContext(operation);
        setEntityContext(entityType, entityId);
    }

    /**
     * Clears the user context from MDC.
     */
    public static void clearUserContext() {
        MDC.remove(USER_ID);
    }

    /**
     * Clears the request context from MDC.
     */
    public static void clearRequestContext() {
        MDC.remove(REQUEST_ID);
    }

    /**
     * Clears the operation context from MDC.
     */
    public static void clearOperationContext() {
        MDC.remove(OPERATION);
    }

    /**
     * Clears the entity context from MDC.
     */
    public static void clearEntityContext() {
        MDC.remove(ENTITY_TYPE);
        MDC.remove(ENTITY_ID);
    }

    /**
     * Clears all custom context from MDC.
     */
    public static void clearAllContext() {
        clearUserContext();
        clearRequestContext();
        clearOperationContext();
        clearEntityContext();
    }

    /**
     * Gets the current user ID from MDC.
     *
     * @return the current user ID or null if not set
     */
    public static String getCurrentUserId() {
        return MDC.get(USER_ID);
    }

    /**
     * Gets the current request ID from MDC.
     *
     * @return the current request ID or null if not set
     */
    public static String getCurrentRequestId() {
        return MDC.get(REQUEST_ID);
    }

    /**
     * Gets the current operation from MDC.
     *
     * @return the current operation or null if not set
     */
    public static String getCurrentOperation() {
        return MDC.get(OPERATION);
    }

    /**
     * Gets the current entity type from MDC.
     *
     * @return the current entity type or null if not set
     */
    public static String getCurrentEntityType() {
        return MDC.get(ENTITY_TYPE);
    }

    /**
     * Gets the current entity ID from MDC.
     *
     * @return the current entity ID or null if not set
     */
    public static String getCurrentEntityId() {
        return MDC.get(ENTITY_ID);
    }
}
package com.library.member.business.logging;

import org.slf4j.MDC;

import java.util.UUID;

public class LoggingContextManager {

    // MDC Keys
    public static final String USER_ID = "userId";
    public static final String REQUEST_ID = "requestId";
    public static final String OPERATION = "operation";
    public static final String ENTITY_TYPE = "entityType";
    public static final String ENTITY_ID = "entityId";

    public static void setUserContext(String userId) {
        if (userId != null && !userId.trim().isEmpty()) {
            MDC.put(USER_ID, userId);
        }
    }

    public static void setRequestContext(String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        MDC.put(REQUEST_ID, requestId);
    }

    public static void setOperationContext(String operation) {
        if (operation != null && !operation.trim().isEmpty()) {
            MDC.put(OPERATION, operation);
        }
    }

    public static void setEntityContext(String entityType, Object entityId) {
        if (entityType != null && !entityType.trim().isEmpty()) {
            MDC.put(ENTITY_TYPE, entityType);
        }
        if (entityId != null) {
            MDC.put(ENTITY_ID, entityId.toString());
        }
    }

    public static void setOperationContext(String userId, String operation, String entityType, Object entityId) {
        setUserContext(userId);
        setOperationContext(operation);
        setEntityContext(entityType, entityId);
    }

    public static void clearUserContext() {
        MDC.remove(USER_ID);
    }

    public static void clearRequestContext() {
        MDC.remove(REQUEST_ID);
    }

    public static void clearOperationContext() {
        MDC.remove(OPERATION);
    }

    public static void clearEntityContext() {
        MDC.remove(ENTITY_TYPE);
        MDC.remove(ENTITY_ID);
    }

    public static void clearAllContext() {
        clearUserContext();
        clearRequestContext();
        clearOperationContext();
        clearEntityContext();
    }

    public static String getCurrentUserId() {
        return MDC.get(USER_ID);
    }

    public static String getCurrentRequestId() {
        return MDC.get(REQUEST_ID);
    }

    public static String getCurrentOperation() {
        return MDC.get(OPERATION);
    }

    public static String getCurrentEntityType() {
        return MDC.get(ENTITY_TYPE);
    }

    public static String getCurrentEntityId() {
        return MDC.get(ENTITY_ID);
    }
}
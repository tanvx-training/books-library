package com.library.loan.aop;

public class AuthorizationException extends EntityServiceException {

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static AuthorizationException insufficientRole(String requiredRole, String userRole) {
        return new AuthorizationException(
            String.format("Insufficient permissions. Required role: %s, User role: %s", requiredRole, userRole)
        );
    }

    public static AuthorizationException accessDenied(String resourceType, String resourceId) {
        return new AuthorizationException(
            String.format("Access denied to %s with ID: %s", resourceType, resourceId)
        );
    }

    public static AuthorizationException operationNotAllowed(String operation) {
        return new AuthorizationException(
            String.format("Operation not allowed: %s", operation)
        );
    }
}
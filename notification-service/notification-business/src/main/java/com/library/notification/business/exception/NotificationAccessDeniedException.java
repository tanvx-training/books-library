package com.library.notification.business.exception;

/**
 * Exception thrown when a user tries to access a notification they don't own
 */
public class NotificationAccessDeniedException extends RuntimeException {

    public NotificationAccessDeniedException(String message) {
        super(message);
    }

    public NotificationAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
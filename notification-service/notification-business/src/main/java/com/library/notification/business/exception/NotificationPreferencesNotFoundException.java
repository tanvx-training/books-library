package com.library.notification.business.exception;

/**
 * Exception thrown when notification preferences are not found for a user
 */
public class NotificationPreferencesNotFoundException extends RuntimeException {

    public NotificationPreferencesNotFoundException(String message) {
        super(message);
    }

    public NotificationPreferencesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
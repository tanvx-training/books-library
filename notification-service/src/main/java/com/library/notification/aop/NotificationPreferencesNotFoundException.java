package com.library.notification.aop;

public class NotificationPreferencesNotFoundException extends RuntimeException {

    public NotificationPreferencesNotFoundException(String message) {
        super(message);
    }

    public NotificationPreferencesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
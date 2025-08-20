package com.library.notification.aop;

public class NotificationAccessDeniedException extends RuntimeException {

    public NotificationAccessDeniedException(String message) {
        super(message);
    }

    public NotificationAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
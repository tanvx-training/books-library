package com.library.notification.business.exception;

/**
 * Exception thrown when an invalid notification status transition is attempted
 */
public class InvalidNotificationStatusException extends RuntimeException {

    public InvalidNotificationStatusException(String message) {
        super(message);
    }

    public InvalidNotificationStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
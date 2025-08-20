package com.library.notification.aop;

public class InvalidNotificationStatusException extends RuntimeException {

    public InvalidNotificationStatusException(String message) {
        super(message);
    }

    public InvalidNotificationStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
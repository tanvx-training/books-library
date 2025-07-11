package com.library.user.application.exception;

/**
 * Base exception for all application-level exceptions in the user context
 */
public class UserApplicationException extends RuntimeException {

    public UserApplicationException(String message) {
        super(message);
    }

    public UserApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
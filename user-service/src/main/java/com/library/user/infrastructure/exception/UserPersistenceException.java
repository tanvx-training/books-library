package com.library.user.infrastructure.exception;

/**
 * Exception thrown when there's an issue with book persistence.
 */
public class UserPersistenceException extends RuntimeException {

    public UserPersistenceException(String message) {
        super(message);
    }

    public UserPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
} 
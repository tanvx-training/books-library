package com.library.user.domain.exception;

/**
 * Exception thrown when a user is not found
 */
public class UserNotFoundException extends DomainException {

    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId);
    }

    public UserNotFoundException(String username) {
        super("User not found with username: " + username);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
package com.library.member.business.exception;


public class AuthenticationException extends EntityServiceException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static AuthenticationException noAuthenticatedUser() {
        return new AuthenticationException("No authenticated user found");
    }

    public static AuthenticationException invalidToken(String reason) {
        return new AuthenticationException("Invalid authentication token: " + reason);
    }

    public static AuthenticationException expiredToken() {
        return new AuthenticationException("Authentication token has expired");
    }

    public static AuthenticationException missingClaim(String claimName) {
        return new AuthenticationException("Authentication token is missing required claim: " + claimName);
    }
}
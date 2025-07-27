package com.library.catalog.business.aop.exception;

/**
 * Exception thrown when an invalid UUID format is encountered.
 * This exception is used for UUID format validation errors and ensures
 * that proper validation messages are provided without exposing internal details.
 */
public class InvalidUuidException extends RuntimeException {
    
    private final String invalidValue;
    private final String parameterName;

    /**
     * Creates an InvalidUuidException with a custom message.
     *
     * @param message the exception message
     */
    public InvalidUuidException(String message) {
        super(message);
        this.invalidValue = null;
        this.parameterName = null;
    }
    
    /**
     * Creates an InvalidUuidException with a custom message and cause.
     *
     * @param message the exception message
     * @param cause the underlying cause
     */
    public InvalidUuidException(String message, Throwable cause) {
        super(message, cause);
        this.invalidValue = null;
        this.parameterName = null;
    }
    
    /**
     * Creates an InvalidUuidException for a specific parameter and invalid value.
     *
     * @param parameterName the name of the parameter that had invalid UUID
     * @param invalidValue the invalid UUID string value
     */
    public InvalidUuidException(String parameterName, String invalidValue) {
        super(invalidValue == null ? 
            String.format("UUID parameter '%s' cannot be null", parameterName) :
            String.format("Invalid UUID format for parameter '%s': %s", parameterName, invalidValue));
        this.parameterName = parameterName;
        this.invalidValue = invalidValue;
    }

    /**
     * Creates an InvalidUuidException for a specific parameter with cause.
     *
     * @param parameterName the name of the parameter that had invalid UUID
     * @param invalidValue the invalid UUID string value
     * @param cause the underlying cause (e.g., IllegalArgumentException from UUID.fromString)
     */
    public InvalidUuidException(String parameterName, String invalidValue, Throwable cause) {
        super(String.format("Invalid UUID format for parameter '%s': %s", parameterName, invalidValue), cause);
        this.parameterName = parameterName;
        this.invalidValue = invalidValue;
    }

    /**
     * Static factory method for creating InvalidUuidException for null UUID parameters.
     *
     * @param parameterName the name of the parameter that was null
     * @return a new InvalidUuidException instance
     */
    public static InvalidUuidException forNullParameter(String parameterName) {
        return new InvalidUuidException(parameterName, (String) null);
    }

    /**
     * Static factory method for creating InvalidUuidException for invalid UUID format.
     *
     * @param parameterName the name of the parameter with invalid format
     * @param invalidValue the invalid UUID string
     * @return a new InvalidUuidException instance
     */
    public static InvalidUuidException forInvalidFormat(String parameterName, String invalidValue) {
        return new InvalidUuidException(parameterName, invalidValue);
    }

    /**
     * Static factory method for creating InvalidUuidException with cause.
     *
     * @param parameterName the name of the parameter with invalid format
     * @param invalidValue the invalid UUID string
     * @param cause the underlying cause
     * @return a new InvalidUuidException instance
     */
    public static InvalidUuidException forInvalidFormat(String parameterName, String invalidValue, Throwable cause) {
        return new InvalidUuidException(parameterName, invalidValue, cause);
    }

    /**
     * Gets the invalid value that caused this exception.
     *
     * @return the invalid UUID string, or null if not applicable
     */
    public String getInvalidValue() {
        return invalidValue;
    }

    /**
     * Gets the parameter name associated with this exception.
     *
     * @return the parameter name, or null if not applicable
     */
    public String getParameterName() {
        return parameterName;
    }
}
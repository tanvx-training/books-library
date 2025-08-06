package com.library.member.business.aop.exception;

import lombok.Getter;

@Getter
public class InvalidUuidException extends RuntimeException {

    private final String invalidValue;

    private final String parameterName;

    public InvalidUuidException(String message) {
        super(message);
        this.invalidValue = null;
        this.parameterName = null;
    }

    public InvalidUuidException(String message, Throwable cause) {
        super(message, cause);
        this.invalidValue = null;
        this.parameterName = null;
    }

    public InvalidUuidException(String parameterName, String invalidValue) {
        super(invalidValue == null ? 
            String.format("UUID parameter '%s' cannot be null", parameterName) :
            String.format("Invalid UUID format for parameter '%s': %s", parameterName, invalidValue));
        this.parameterName = parameterName;
        this.invalidValue = invalidValue;
    }

    public InvalidUuidException(String parameterName, String invalidValue, Throwable cause) {
        super(String.format("Invalid UUID format for parameter '%s': %s", parameterName, invalidValue), cause);
        this.parameterName = parameterName;
        this.invalidValue = invalidValue;
    }

    public static InvalidUuidException forNullParameter(String parameterName) {
        return new InvalidUuidException(parameterName, (String) null);
    }

    public static InvalidUuidException forInvalidFormat(String parameterName, String invalidValue) {
        return new InvalidUuidException(parameterName, invalidValue);
    }

    public static InvalidUuidException forInvalidFormat(String parameterName, String invalidValue, Throwable cause) {
        return new InvalidUuidException(parameterName, invalidValue, cause);
    }

}
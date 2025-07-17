package com.library.book.domain.exception;

import lombok.Getter;

@Getter
public class InvalidBookCopyDataException extends RuntimeException {
    private final String field;
    private final String reason;

    public InvalidBookCopyDataException(String message) {
        super(message);
        this.field = null;
        this.reason = message;
    }
} 
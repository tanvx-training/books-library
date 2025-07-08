package com.library.book.domain.exception;

import lombok.Getter;

@Getter
public class InvalidBookCopyDataException extends DomainException {
    private final String field;
    
    public InvalidBookCopyDataException(String field, String message) {
        super(message);
        this.field = field;
    }

} 
package com.library.book.domain.exception;

/**
 * Exception thrown when an invalid operation is attempted on a BookCopy
 */
public class InvalidBookCopyOperationException extends BookDomainException {
    
    public InvalidBookCopyOperationException(String message) {
        super(message);
    }
    
    public InvalidBookCopyOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
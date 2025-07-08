package com.library.book.application.exception;

public class BookCopyApplicationException extends RuntimeException {
    
    public BookCopyApplicationException(String message) {
        super(message);
    }
    
    public BookCopyApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
} 
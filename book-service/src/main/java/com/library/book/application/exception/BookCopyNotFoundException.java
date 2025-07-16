package com.library.book.application.exception;

/**
 * Exception thrown when a book copy is not found
 */
public class BookCopyNotFoundException extends BookApplicationException {
    
    public BookCopyNotFoundException(Long bookCopyId) {
        super("Book copy not found with ID: " + bookCopyId);
    }
    
    public BookCopyNotFoundException(String message) {
        super(message);
    }
    
    public BookCopyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
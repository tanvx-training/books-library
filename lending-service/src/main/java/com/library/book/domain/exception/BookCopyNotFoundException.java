package com.library.book.domain.exception;

public class BookCopyNotFoundException extends RuntimeException {
    
    public BookCopyNotFoundException(Long id) {
        super("Book copy with ID " + id + " not found");
    }
    
    public BookCopyNotFoundException(String message) {
        super(message);
    }
} 
package com.library.book.application.exception;

import lombok.Getter;

/**
 * Exception thrown when a book cannot be found.
 */
@Getter
public class BookNotFoundException extends BookApplicationException {

    private final Object bookId;

    public BookNotFoundException(Object bookId) {
        super(String.format("Book with ID '%s' not found", bookId));
        this.bookId = bookId;
    }
} 
package com.library.book.application.exception;

import lombok.Getter;

/**
 * Exception thrown when an author cannot be found.
 */
@Getter
public class AuthorNotFoundException extends AuthorApplicationException {

    private final Object authorId;

    public AuthorNotFoundException(Object authorId) {
        super(String.format("Author with ID '%s' not found", authorId));
        this.authorId = authorId;
    }

}
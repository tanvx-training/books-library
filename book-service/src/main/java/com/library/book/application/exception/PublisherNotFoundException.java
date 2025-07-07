package com.library.book.application.exception;

import lombok.Getter;

/**
 * Exception thrown when a publisher cannot be found.
 */
@Getter
public class PublisherNotFoundException extends PublisherApplicationException {

    private final Object publisherId;

    public PublisherNotFoundException(Object publisherId) {
        super(String.format("Publisher with ID '%s' not found", publisherId));
        this.publisherId = publisherId;
    }

}
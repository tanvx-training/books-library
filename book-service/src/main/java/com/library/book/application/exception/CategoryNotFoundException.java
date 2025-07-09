package com.library.book.application.exception;

import lombok.Getter;

/**
 * Exception thrown when a category cannot be found.
 */
@Getter
public class CategoryNotFoundException extends CategoryApplicationException {

    private final Object categoryId;

    public CategoryNotFoundException(Object categoryId) {
        super(String.format("Category with ID '%s' not found", categoryId));
        this.categoryId = categoryId;
    }
}
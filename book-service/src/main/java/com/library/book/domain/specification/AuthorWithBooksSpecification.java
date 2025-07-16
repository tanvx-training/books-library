package com.library.book.domain.specification;

import com.library.book.domain.model.author.Author;

/**
 * Specification to check if an author has published books
 */
public class AuthorWithBooksSpecification implements AuthorSpecification {
    
    private final int minimumBookCount;
    
    public AuthorWithBooksSpecification() {
        this(1);
    }
    
    public AuthorWithBooksSpecification(int minimumBookCount) {
        this.minimumBookCount = minimumBookCount;
    }
    
    @Override
    public boolean isSatisfiedBy(Author author) {
        return author.getBookCount() >= minimumBookCount;
    }
}
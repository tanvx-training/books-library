package com.library.book.domain.specification;

import com.library.book.domain.model.book.Book;

/**
 * Base interface for Book specifications
 */
public interface BookSpecification {
    boolean isSatisfiedBy(Book book);
    
    default BookSpecification and(BookSpecification other) {
        return new AndSpecification(this, other);
    }
    
    default BookSpecification or(BookSpecification other) {
        return new OrSpecification(this, other);
    }
    
    default BookSpecification not() {
        return new NotSpecification(this);
    }
}

class AndSpecification implements BookSpecification {
    private final BookSpecification left;
    private final BookSpecification right;
    
    public AndSpecification(BookSpecification left, BookSpecification right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public boolean isSatisfiedBy(Book book) {
        return left.isSatisfiedBy(book) && right.isSatisfiedBy(book);
    }
}

class OrSpecification implements BookSpecification {
    private final BookSpecification left;
    private final BookSpecification right;
    
    public OrSpecification(BookSpecification left, BookSpecification right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public boolean isSatisfiedBy(Book book) {
        return left.isSatisfiedBy(book) || right.isSatisfiedBy(book);
    }
}

class NotSpecification implements BookSpecification {
    private final BookSpecification specification;
    
    public NotSpecification(BookSpecification specification) {
        this.specification = specification;
    }
    
    @Override
    public boolean isSatisfiedBy(Book book) {
        return !specification.isSatisfiedBy(book);
    }
}
package com.library.book.domain.specification;

import com.library.book.domain.model.author.Author;

/**
 * Base interface for Author specifications
 */
public interface AuthorSpecification {
    boolean isSatisfiedBy(Author author);
    
    default AuthorSpecification and(AuthorSpecification other) {
        return new AndAuthorSpecification(this, other);
    }
    
    default AuthorSpecification or(AuthorSpecification other) {
        return new OrAuthorSpecification(this, other);
    }
    
    default AuthorSpecification not() {
        return new NotAuthorSpecification(this);
    }
}

class AndAuthorSpecification implements AuthorSpecification {
    private final AuthorSpecification left;
    private final AuthorSpecification right;
    
    public AndAuthorSpecification(AuthorSpecification left, AuthorSpecification right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public boolean isSatisfiedBy(Author author) {
        return left.isSatisfiedBy(author) && right.isSatisfiedBy(author);
    }
}

class OrAuthorSpecification implements AuthorSpecification {
    private final AuthorSpecification left;
    private final AuthorSpecification right;
    
    public OrAuthorSpecification(AuthorSpecification left, AuthorSpecification right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public boolean isSatisfiedBy(Author author) {
        return left.isSatisfiedBy(author) || right.isSatisfiedBy(author);
    }
}

class NotAuthorSpecification implements AuthorSpecification {
    private final AuthorSpecification specification;
    
    public NotAuthorSpecification(AuthorSpecification specification) {
        this.specification = specification;
    }
    
    @Override
    public boolean isSatisfiedBy(Author author) {
        return !specification.isSatisfiedBy(author);
    }
}
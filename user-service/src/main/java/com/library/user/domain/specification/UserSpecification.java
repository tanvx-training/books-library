package com.library.user.domain.specification;

import com.library.user.domain.model.user.User;

/**
 * Base specification interface for User domain
 * Implements Specification pattern for encapsulating business rules
 */
public interface UserSpecification {
    
    /**
     * Check if the user satisfies this specification
     */
    boolean isSatisfiedBy(User user);
    
    /**
     * Combine this specification with another using AND logic
     */
    default UserSpecification and(UserSpecification other) {
        return new AndUserSpecification(this, other);
    }
    
    /**
     * Combine this specification with another using OR logic
     */
    default UserSpecification or(UserSpecification other) {
        return new OrUserSpecification(this, other);
    }
    
    /**
     * Negate this specification
     */
    default UserSpecification not() {
        return new NotUserSpecification(this);
    }
}
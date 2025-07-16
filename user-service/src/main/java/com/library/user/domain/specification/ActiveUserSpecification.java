package com.library.user.domain.specification;

import com.library.user.domain.model.user.User;

/**
 * Specification to check if user is active and eligible for operations
 */
public class ActiveUserSpecification implements UserSpecification {
    
    @Override
    public boolean isSatisfiedBy(User user) {
        return user.isActive() && 
               user.getUsername() != null && 
               user.getEmail() != null;
    }
}
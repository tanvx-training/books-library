package com.library.user.domain.specification;

import com.library.user.domain.model.user.User;
import lombok.RequiredArgsConstructor;

/**
 * NOT specification
 */
@RequiredArgsConstructor
public class NotUserSpecification implements UserSpecification {
    
    private final UserSpecification specification;
    
    @Override
    public boolean isSatisfiedBy(User user) {
        return !specification.isSatisfiedBy(user);
    }
}
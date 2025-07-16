package com.library.user.domain.specification;

import com.library.user.domain.model.user.User;
import lombok.RequiredArgsConstructor;

/**
 * AND composite specification
 */
@RequiredArgsConstructor
public class AndUserSpecification implements UserSpecification {
    
    private final UserSpecification left;
    private final UserSpecification right;
    
    @Override
    public boolean isSatisfiedBy(User user) {
        return left.isSatisfiedBy(user) && right.isSatisfiedBy(user);
    }
}
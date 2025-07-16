package com.library.user.domain.specification;

import com.library.user.domain.model.user.User;
import com.library.user.domain.repository.LibraryCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Specification to check if user is eligible for library card
 */
@Component
@RequiredArgsConstructor
public class EligibleForLibraryCardSpecification implements UserSpecification {
    
    private final LibraryCardRepository libraryCardRepository;
    
    @Override
    public boolean isSatisfiedBy(User user) {
        // User must be active
        if (!user.isActive()) {
            return false;
        }
        
        // User must not have an active library card already
        boolean hasActiveCard = libraryCardRepository.findByUserId(user.getId())
                .stream()
                .anyMatch(card -> card.getStatus() == com.library.user.domain.model.librarycard.CardStatus.ACTIVE);
                
        return !hasActiveCard;
    }
}
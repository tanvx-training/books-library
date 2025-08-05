package com.library.member.repository.enums;

/**
 * Enum representing the status of a library card.
 * Defines the various states a library card can be in within the library system.
 */
public enum LibraryCardStatus {
    
    /**
     * Card is active and can be used for library services
     */
    ACTIVE,
    
    /**
     * Card is temporarily inactive but can be reactivated
     */
    INACTIVE,
    
    /**
     * Card has expired and needs renewal
     */
    EXPIRED,
    
    /**
     * Card has been reported as lost
     */
    LOST,
    
    /**
     * Card has been blocked due to violations or administrative action
     */
    BLOCKED;

    /**
     * Check if the card status allows library services
     * @return true if the card can be used for borrowing and other services
     */
    public boolean canUseLibraryServices() {
        return this == ACTIVE;
    }

    /**
     * Check if the card can be reactivated
     * @return true if the card status allows reactivation
     */
    public boolean canBeReactivated() {
        return this == INACTIVE || this == EXPIRED;
    }

    /**
     * Check if the card requires replacement
     * @return true if the card needs to be replaced
     */
    public boolean requiresReplacement() {
        return this == LOST;
    }

    /**
     * Check if the card is in a problematic state
     * @return true if the card has issues that prevent normal use
     */
    public boolean hasIssues() {
        return this == LOST || this == BLOCKED || this == EXPIRED;
    }

    /**
     * Check if the card can be renewed
     * @return true if the card status allows renewal
     */
    public boolean canBeRenewed() {
        return this == ACTIVE || this == EXPIRED;
    }
}
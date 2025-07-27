package com.library.catalog.repository.enums;

/**
 * Enumeration representing the possible status values for a book copy.
 * This enum defines the lifecycle states of physical book copies in the library inventory.
 */
public enum BookCopyStatus {
    /**
     * The book copy is available for borrowing
     */
    AVAILABLE,
    
    /**
     * The book copy is currently borrowed by a member
     */
    BORROWED,
    
    /**
     * The book copy is reserved for a specific member
     */
    RESERVED,
    
    /**
     * The book copy has been lost and is not available
     */
    LOST,
    
    /**
     * The book copy is damaged and not available for borrowing
     */
    DAMAGED
}
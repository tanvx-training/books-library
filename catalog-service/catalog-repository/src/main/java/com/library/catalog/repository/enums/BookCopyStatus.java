package com.library.catalog.repository.enums;

/**
 * Enum representing the status of a book copy.
 * Defines the various states a book copy can be in within the library system.
 */
public enum BookCopyStatus {
    
    /**
     * Copy is available for borrowing
     */
    AVAILABLE,
    
    /**
     * Copy is currently borrowed by a user
     */
    BORROWED,
    
    /**
     * Copy is reserved for a specific user
     */
    RESERVED,
    
    /**
     * Copy is under maintenance and not available for circulation
     */
    MAINTENANCE,
    
    /**
     * Copy has been lost and is no longer in the library's possession
     */
    LOST;

    /**
     * Check if the status allows the copy to be borrowed
     * @return true if the copy can be borrowed in this status
     */
    public boolean canBeBorrowed() {
        return this == AVAILABLE;
    }

    /**
     * Check if the status allows the copy to be reserved
     * @return true if the copy can be reserved in this status
     */
    public boolean canBeReserved() {
        return this == AVAILABLE || this == BORROWED;
    }

    /**
     * Check if the status indicates the copy is in circulation
     * @return true if the copy is actively being used
     */
    public boolean isInCirculation() {
        return this == BORROWED || this == RESERVED;
    }

    /**
     * Check if the status indicates the copy is unavailable
     * @return true if the copy cannot be borrowed
     */
    public boolean isUnavailable() {
        return this == MAINTENANCE || this == LOST;
    }
}
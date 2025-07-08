package com.library.book.domain.model.bookcopy;

/**
 * Enum representing possible statuses of a book copy.
 */
public enum BookCopyStatus {
    AVAILABLE,   // Available for borrowing
    BORROWED,    // Currently borrowed
    RESERVED,    // Reserved
    MAINTENANCE, // Under maintenance
    LOST,        // Lost
    DAMAGED;     // Damaged
    
    public static boolean isValid(String status) {
        try {
            BookCopyStatus.valueOf(status);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    public static BookCopyStatus fromString(String status) {
        return BookCopyStatus.valueOf(status);
    }
} 
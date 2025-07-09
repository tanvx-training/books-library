package com.library.book.domain.model.bookcopy;

/**
 * Enum representing possible conditions of a book copy.
 */
public enum BookCopyCondition {
    NEW,           // Brand new
    EXCELLENT,     // Like new
    GOOD,          // Shows some wear but intact
    FAIR,          // Noticeable wear
    POOR,          // Significant wear, still usable
    UNUSABLE;      // Cannot be used anymore
    
    public static boolean isValid(String condition) {
        if (condition == null || condition.isEmpty()) {
            return true; // Null/empty condition is allowed
        }
        
        try {
            BookCopyCondition.valueOf(condition);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    public static BookCopyCondition fromString(String condition) {
        if (condition == null || condition.isEmpty()) {
            return null;
        }
        return BookCopyCondition.valueOf(condition);
    }
} 
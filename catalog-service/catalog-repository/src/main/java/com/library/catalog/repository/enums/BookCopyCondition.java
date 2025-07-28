package com.library.catalog.repository.enums;

/**
 * Enum representing the physical condition of a book copy.
 * Defines the various physical states a book copy can be in.
 */
public enum BookCopyCondition {
    
    /**
     * Brand new condition, no wear or damage
     */
    NEW,
    
    /**
     * Good condition with minimal wear
     */
    GOOD,
    
    /**
     * Fair condition with some wear but still functional
     */
    FAIR,
    
    /**
     * Poor condition with significant wear but still usable
     */
    POOR,
    
    /**
     * Damaged condition, not suitable for normal circulation
     */
    DAMAGED;

    /**
     * Check if the condition allows the copy to be borrowed
     * @return true if the copy can be borrowed in this condition
     */
    public boolean canBeBorrowed() {
        return this != DAMAGED;
    }

    /**
     * Check if the condition requires special handling
     * @return true if the copy needs special care
     */
    public boolean requiresSpecialHandling() {
        return this == POOR || this == DAMAGED;
    }

    /**
     * Check if the condition is considered good for circulation
     * @return true if the copy is in good condition for borrowing
     */
    public boolean isGoodForCirculation() {
        return this == NEW || this == GOOD || this == FAIR;
    }

    /**
     * Get the display name for the condition
     * @return user-friendly display name
     */
    public String getDisplayName() {
        return switch (this) {
            case NEW -> "New";
            case GOOD -> "Good";
            case FAIR -> "Fair";
            case POOR -> "Poor";
            case DAMAGED -> "Damaged";
        };
    }

    /**
     * Get the priority order for condition (lower number = better condition)
     * @return priority order number
     */
    public int getPriority() {
        return switch (this) {
            case NEW -> 1;
            case GOOD -> 2;
            case FAIR -> 3;
            case POOR -> 4;
            case DAMAGED -> 5;
        };
    }
}
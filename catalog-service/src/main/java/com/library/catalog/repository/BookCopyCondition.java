package com.library.catalog.repository;

public enum BookCopyCondition {

    NEW,
    GOOD,
    FAIR,
    POOR,
    DAMAGED;

    public boolean canBeBorrowed() {
        return this != DAMAGED;
    }

    public boolean requiresSpecialHandling() {
        return this == POOR || this == DAMAGED;
    }

    public boolean isGoodForCirculation() {
        return this == NEW || this == GOOD || this == FAIR;
    }

    public String getDisplayName() {
        return switch (this) {
            case NEW -> "New";
            case GOOD -> "Good";
            case FAIR -> "Fair";
            case POOR -> "Poor";
            case DAMAGED -> "Damaged";
        };
    }

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
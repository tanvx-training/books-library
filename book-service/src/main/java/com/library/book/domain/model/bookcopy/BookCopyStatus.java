package com.library.book.domain.model.bookcopy;

public enum BookCopyStatus {
    AVAILABLE("Available for borrowing"),
    BORROWED("Currently borrowed"),
    RESERVED("Reserved for a user"),
    MAINTENANCE("Under maintenance"),
    LOST("Lost or missing"),
    DAMAGED("Damaged and unusable");

    private final String description;

    BookCopyStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailableForBorrowing() {
        return this == AVAILABLE;
    }

    public boolean isAvailableForReservation() {
        return this == AVAILABLE || this == BORROWED;
    }

    public boolean canBeReturned() {
        return this == BORROWED;
    }

    public boolean requiresMaintenance() {
        return this == MAINTENANCE || this == DAMAGED;
    }
}
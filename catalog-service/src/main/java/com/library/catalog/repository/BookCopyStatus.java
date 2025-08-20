package com.library.catalog.repository;

public enum BookCopyStatus {

    AVAILABLE,

    BORROWED,

    RESERVED,

    MAINTENANCE,

    LOST;

    public boolean canBeBorrowed() {
        return this == AVAILABLE;
    }

    public boolean canBeReserved() {
        return this == AVAILABLE || this == BORROWED;
    }

    public boolean isInCirculation() {
        return this == BORROWED || this == RESERVED;
    }

    public boolean isUnavailable() {
        return this == MAINTENANCE || this == LOST;
    }
}
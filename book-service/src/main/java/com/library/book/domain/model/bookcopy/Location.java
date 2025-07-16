package com.library.book.domain.model.bookcopy;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Location {
    private final String value;

    public Location(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be null or empty");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Location cannot exceed 50 characters");
        }
        this.value = value.trim();
    }

    public static Location of(String value) {
        return new Location(value);
    }

    public static Location empty() {
        return new Location("Unknown");
    }

    @Override
    public String toString() {
        return value;
    }
}
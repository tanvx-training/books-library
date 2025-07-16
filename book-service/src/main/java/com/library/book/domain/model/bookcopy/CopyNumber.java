package com.library.book.domain.model.bookcopy;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class CopyNumber {
    private final String value;

    public CopyNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Copy number cannot be null or empty");
        }
        if (value.length() > 20) {
            throw new IllegalArgumentException("Copy number cannot exceed 20 characters");
        }
        this.value = value.trim();
    }

    public static CopyNumber of(String value) {
        return new CopyNumber(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
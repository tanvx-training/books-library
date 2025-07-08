package com.library.book.domain.model.book;

import com.library.book.domain.exception.InvalidBookDataException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class ISBN {

    private String value;

    private ISBN(String value) {
        this.value = value;
    }

    public static ISBN of(String isbn) {
        validate(isbn);
        return new ISBN(isbn);
    }

    private static void validate(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new InvalidBookDataException("isbn", "ISBN cannot be empty");
        }
        if (isbn.length() > 20) {
            throw new InvalidBookDataException("isbn", "ISBN cannot exceed 20 characters");
        }
        // Additional validation could be added here (e.g., ISBN-10 or ISBN-13 format)
    }

    @Override
    public String toString() {
        return value;
    }
}

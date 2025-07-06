package com.library.book.domain.model.author;

import com.library.book.domain.exception.InvalidAuthorDataException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class AuthorName {

    private String value;

    private AuthorName(String value) {
        this.value = value;
    }

    public static AuthorName of(String name) {
        validate(name);
        return new AuthorName(name);
    }

    private static void validate(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidAuthorDataException("name", "Author name cannot be empty");
        }
        if (name.length() > 100) {
            throw new InvalidAuthorDataException("name", "Author name cannot exceed 100 characters");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}

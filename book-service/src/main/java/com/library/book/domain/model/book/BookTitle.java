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
public class BookTitle {

    private String value;

    private BookTitle(String value) {
        this.value = value;
    }

    public static BookTitle of(String title) {
        validate(title);
        return new BookTitle(title);
    }

    private static void validate(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidBookDataException("title", "Book title cannot be empty");
        }
        if (title.length() > 200) {
            throw new InvalidBookDataException("title", "Book title cannot exceed 200 characters");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}

package com.library.book.domain.model.bookcopy;

import com.library.book.domain.exception.InvalidBookCopyDataException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class BookReference {
    private Long bookId;
    private String title;
    
    private BookReference(Long bookId, String title) {
        this.bookId = bookId;
        this.title = title;
    }
    
    public static BookReference of(Long bookId, String title) {
        validate(bookId);
        return new BookReference(bookId, title);
    }
    
    private static void validate(Long bookId) {
        if (bookId == null) {
            throw new InvalidBookCopyDataException("bookId", "Book ID cannot be null");
        }
    }
} 
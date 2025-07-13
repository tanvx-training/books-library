package com.library.book.domain.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BookDeletedEvent extends AuditEvent {
    
    public BookDeletedEvent(String bookId, String bookData) {
        super("BOOK_DELETED", "Book", bookId);
        setOldValue(bookData);
    }
}
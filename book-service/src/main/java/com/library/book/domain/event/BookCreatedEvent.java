package com.library.book.domain.event;

import com.library.book.domain.model.shared.AuditEvent;
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
public class BookCreatedEvent extends AuditEvent {

    public BookCreatedEvent(String bookId, String bookData) {
        super("BOOK_CREATED", "Book", bookId);
        setNewValue(bookData);
    }
}
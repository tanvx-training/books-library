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
public class BookUpdatedEvent extends AuditEvent {
    
    public BookUpdatedEvent(String bookId, String oldData, String newData, String changes) {
        super("BOOK_UPDATED", "Book", bookId);
        setOldValue(oldData);
        setNewValue(newData);
        setChanges(changes);
    }
}
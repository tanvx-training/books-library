package com.library.book.domain.event;

import com.library.book.domain.model.bookcopy.BookCopy;
import com.library.book.domain.model.shared.DomainEvent;
import lombok.Getter;

@Getter
public class BookCopyCreatedEvent extends DomainEvent {
    private final Long bookCopyId;
    private final Long bookId;
    private final String copyNumber;
    private final String status;
    
    public BookCopyCreatedEvent(BookCopy bookCopy) {
        this.bookCopyId = bookCopy.getId() != null ? bookCopy.getId().getValue() : null;
        this.bookId = bookCopy.getBookReference().getBookId();
        this.copyNumber = bookCopy.getCopyNumber().getValue();
        this.status = bookCopy.getStatus().name();
    }
} 
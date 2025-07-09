package com.library.book.domain.event;

import com.library.book.domain.model.bookcopy.BookCopy;
import com.library.book.domain.model.bookcopy.BookCopyStatus;
import com.library.book.domain.model.shared.DomainEvent;
import lombok.Getter;

@Getter
public class BookCopyStatusChangedEvent extends DomainEvent {
    private final Long bookCopyId;
    private final Long bookId;
    private final String copyNumber;
    private final String oldStatus;
    private final String newStatus;
    
    public BookCopyStatusChangedEvent(BookCopy bookCopy, BookCopyStatus oldStatus, BookCopyStatus newStatus) {
        this.bookCopyId = bookCopy.getId().getValue();
        this.bookId = bookCopy.getBookReference().getBookId();
        this.copyNumber = bookCopy.getCopyNumber().getValue();
        this.oldStatus = oldStatus.name();
        this.newStatus = newStatus.name();
    }
} 
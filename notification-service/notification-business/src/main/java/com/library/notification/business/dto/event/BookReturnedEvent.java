package com.library.notification.business.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Event fired when a book is returned
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BookReturnedEvent extends LibraryEvent {
    
    private UUID bookPublicId;
    private String bookTitle;
    private String bookIsbn;
    private LocalDate returnDate;
    private UUID loanPublicId;
    private boolean wasOverdue;
}
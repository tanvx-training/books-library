package com.library.notification.business.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Event fired when a book becomes overdue
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BookOverdueEvent extends LibraryEvent {
    
    private UUID bookPublicId;
    private String bookTitle;
    private String bookIsbn;
    private LocalDate dueDate;
    private LocalDate currentDate;
    private int daysOverdue;
    private UUID loanPublicId;
}
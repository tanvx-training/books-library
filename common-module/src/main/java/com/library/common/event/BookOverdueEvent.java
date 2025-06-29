package com.library.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Payload for BOOK_OVERDUE events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookOverdueEvent {
    private Long userId;
    private String userEmail;
    private String userName;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String isbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private int daysOverdue;
    private double fineAmount;
} 
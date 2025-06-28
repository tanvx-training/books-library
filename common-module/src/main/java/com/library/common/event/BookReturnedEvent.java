package com.library.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Payload for BOOK_RETURNED events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookReturnedEvent {
    private Long userId;
    private String userEmail;
    private String userName;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String isbn;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private LocalDate dueDate;
    private boolean isLate;
    private int daysLate;
} 
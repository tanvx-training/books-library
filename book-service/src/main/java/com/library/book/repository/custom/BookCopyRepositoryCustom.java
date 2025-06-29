package com.library.book.repository.custom;

import com.library.book.model.BookCopy;
import com.library.book.utils.enums.BookCopyStatus;

import java.util.List;
import java.util.Map;

/**
 * Custom repository interface for advanced BookCopy operations with logging
 */
public interface BookCopyRepositoryCustom {
    
    /**
     * Find available book copies by book ID with status filtering
     */
    List<BookCopy> findAvailableBookCopiesByBookId(Long bookId);
    
    /**
     * Get book copy statistics by status
     */
    Map<BookCopyStatus, Long> getBookCopyStatisticsByStatus();
    
    /**
     * Find book copies by multiple statuses
     */
    List<BookCopy> findBookCopiesByStatuses(List<BookCopyStatus> statuses);
    
    /**
     * Update multiple book copy statuses in batch
     */
    int batchUpdateBookCopyStatus(List<Long> bookCopyIds, BookCopyStatus newStatus);
    
    /**
     * Find overdue book copies (borrowed but not returned within time limit)
     */
    List<BookCopy> findOverdueBookCopies(int daysOverdue);
    
    /**
     * Get book copy availability report for a specific book
     */
    Map<String, Object> getBookCopyAvailabilityReport(Long bookId);
} 
package com.library.book.domain.repository;

import com.library.book.domain.model.book.BookId;
import com.library.book.domain.model.bookcopy.BookCopy;
import com.library.book.domain.model.bookcopy.BookCopyId;
import com.library.book.domain.model.bookcopy.BookCopyStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BookCopy aggregate
 */
public interface BookCopyRepository {
    
    /**
     * Save a book copy
     */
    BookCopy save(BookCopy bookCopy);
    
    /**
     * Find book copy by ID
     */
    Optional<BookCopy> findById(BookCopyId id);
    
    /**
     * Find all copies of a specific book
     */
    List<BookCopy> findByBookId(BookId bookId);
    
    /**
     * Find available copies of a specific book
     */
    List<BookCopy> findAvailableCopiesByBookId(BookId bookId);
    
    /**
     * Find copies by status
     */
    Page<BookCopy> findByStatus(BookCopyStatus status, int page, int size);
    
    /**
     * Find copies borrowed by a specific user
     */
    List<BookCopy> findBorrowedByUser(String userKeycloakId);
    
    /**
     * Find overdue copies
     */
    List<BookCopy> findOverdueCopies(LocalDateTime currentDate);
    
    /**
     * Find copies due soon (within specified days)
     */
    List<BookCopy> findCopiesDueSoon(LocalDateTime fromDate, LocalDateTime toDate);
    
    /**
     * Check if a copy number exists for a book
     */
    boolean existsByBookIdAndCopyNumber(BookId bookId, String copyNumber);
    
    /**
     * Count available copies for a book
     */
    long countAvailableCopiesByBookId(BookId bookId);
    
    /**
     * Count total copies for a book
     */
    long countByBookId(BookId bookId);
    
    /**
     * Find all copies with pagination
     */
    Page<BookCopy> findAll(int page, int size);
    
    /**
     * Delete a book copy (hard delete)
     */
    void delete(BookCopy bookCopy);
}
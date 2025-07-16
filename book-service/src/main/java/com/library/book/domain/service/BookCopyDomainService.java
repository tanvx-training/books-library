package com.library.book.domain.service;

import com.library.book.domain.model.book.BookId;
import com.library.book.domain.model.bookcopy.BookCopy;
import com.library.book.domain.model.bookcopy.BookCopyId;
import com.library.book.domain.model.bookcopy.BookCopyStatus;
import com.library.book.domain.repository.BookCopyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Domain service for BookCopy-related business logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookCopyDomainService {
    
    private final BookCopyRepository bookCopyRepository;
    
    /**
     * Borrow a book copy with business rules validation
     */
    public void borrowBookCopy(BookCopyId bookCopyId, String borrowerKeycloakId, int loanPeriodDays) {
        Optional<BookCopy> bookCopyOpt = bookCopyRepository.findById(bookCopyId);
        if (!bookCopyOpt.isPresent()) {
            throw new IllegalArgumentException("Book copy not found: " + bookCopyId);
        }
        
        BookCopy bookCopy = bookCopyOpt.get();
        LocalDateTime dueDate = LocalDateTime.now().plusDays(loanPeriodDays);
        
        // Business logic is handled in the aggregate
        bookCopy.borrowTo(borrowerKeycloakId, dueDate);
        
        bookCopyRepository.save(bookCopy);
        log.info("Book copy {} borrowed by user {}", bookCopyId, borrowerKeycloakId);
    }
    
    /**
     * Return a book copy
     */
    public void returnBookCopy(BookCopyId bookCopyId) {
        Optional<BookCopy> bookCopyOpt = bookCopyRepository.findById(bookCopyId);
        if (!bookCopyOpt.isPresent()) {
            throw new IllegalArgumentException("Book copy not found: " + bookCopyId);
        }
        
        BookCopy bookCopy = bookCopyOpt.get();
        String previousBorrower = bookCopy.getCurrentBorrowerKeycloakId();
        
        // Business logic is handled in the aggregate
        bookCopy.returnCopy();
        
        bookCopyRepository.save(bookCopy);
        log.info("Book copy {} returned by user {}", bookCopyId, previousBorrower);
    }
    
    /**
     * Reserve a book copy
     */
    public void reserveBookCopy(BookCopyId bookCopyId, String reserverKeycloakId) {
        Optional<BookCopy> bookCopyOpt = bookCopyRepository.findById(bookCopyId);
        if (!bookCopyOpt.isPresent()) {
            throw new IllegalArgumentException("Book copy not found: " + bookCopyId);
        }
        
        BookCopy bookCopy = bookCopyOpt.get();
        
        // Business logic is handled in the aggregate
        bookCopy.reserve(reserverKeycloakId);
        
        bookCopyRepository.save(bookCopy);
        log.info("Book copy {} reserved by user {}", bookCopyId, reserverKeycloakId);
    }
    
    /**
     * Find the best available copy for a specific book
     */
    public Optional<BookCopy> findBestAvailableCopyForBook(BookId bookId) {
        List<BookCopy> availableCopies = bookCopyRepository.findAvailableCopiesByBookId(bookId);
        
        return availableCopies.stream()
            .filter(BookCopy::canBeBorrowed)
            .min((copy1, copy2) -> {
                // Prioritize better condition
                int conditionComparison = copy1.getCondition().compareTo(copy2.getCondition());
                if (conditionComparison != 0) {
                    return conditionComparison;
                }
                // Then by copy number
                return copy1.getCopyNumber().getValue().compareTo(copy2.getCopyNumber().getValue());
            });
    }
    
    /**
     * Get all copies borrowed by a user
     */
    public List<BookCopy> getUserBorrowedCopies(String userKeycloakId) {
        return bookCopyRepository.findBorrowedByUser(userKeycloakId);
    }
    
    /**
     * Check if user has overdue books
     */
    public boolean hasOverdueBooks(String userKeycloakId) {
        List<BookCopy> borrowedCopies = getUserBorrowedCopies(userKeycloakId);
        return borrowedCopies.stream().anyMatch(BookCopy::isOverdue);
    }
    
    /**
     * Get overdue copies for a user
     */
    public List<BookCopy> getUserOverdueCopies(String userKeycloakId) {
        List<BookCopy> borrowedCopies = getUserBorrowedCopies(userKeycloakId);
        return borrowedCopies.stream()
            .filter(BookCopy::isOverdue)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Calculate fine for overdue books
     */
    public double calculateOverdueFine(BookCopyId bookCopyId, double dailyFineRate) {
        Optional<BookCopy> bookCopyOpt = bookCopyRepository.findById(bookCopyId);
        if (!bookCopyOpt.isPresent()) {
            return 0.0;
        }
        
        BookCopy bookCopy = bookCopyOpt.get();
        if (!bookCopy.isOverdue()) {
            return 0.0;
        }
        
        LocalDateTime dueDate = bookCopy.getDueDate();
        LocalDateTime now = LocalDateTime.now();
        
        long overdueDays = java.time.Duration.between(dueDate, now).toDays();
        return overdueDays * dailyFineRate;
    }
    
    /**
     * Bulk update status for maintenance
     */
    public void markCopiesForMaintenance(List<BookCopyId> bookCopyIds) {
        for (BookCopyId bookCopyId : bookCopyIds) {
            Optional<BookCopy> bookCopyOpt = bookCopyRepository.findById(bookCopyId);
            if (bookCopyOpt.isPresent()) {
                BookCopy bookCopy = bookCopyOpt.get();
                try {
                    bookCopy.markForMaintenance();
                    bookCopyRepository.save(bookCopy);
                    log.info("Book copy {} marked for maintenance", bookCopyId);
                } catch (Exception e) {
                    log.warn("Could not mark book copy {} for maintenance: {}", bookCopyId, e.getMessage());
                }
            }
        }
    }
    
    /**
     * Get statistics for a book
     */
    public BookCopyStatistics getBookCopyStatistics(BookId bookId) {
        List<BookCopy> allCopies = bookCopyRepository.findByBookId(bookId);
        
        long totalCopies = allCopies.size();
        long availableCopies = allCopies.stream()
            .mapToLong(copy -> copy.canBeBorrowed() ? 1 : 0)
            .sum();
        long borrowedCopies = allCopies.stream()
            .mapToLong(copy -> copy.getStatus() == BookCopyStatus.BORROWED ? 1 : 0)
            .sum();
        long reservedCopies = allCopies.stream()
            .mapToLong(copy -> copy.getStatus() == BookCopyStatus.RESERVED ? 1 : 0)
            .sum();
        long maintenanceCopies = allCopies.stream()
            .mapToLong(copy -> copy.getStatus() == BookCopyStatus.MAINTENANCE ? 1 : 0)
            .sum();
        
        return BookCopyStatistics.builder()
            .bookId(bookId.getValue())
            .totalCopies(totalCopies)
            .availableCopies(availableCopies)
            .borrowedCopies(borrowedCopies)
            .reservedCopies(reservedCopies)
            .maintenanceCopies(maintenanceCopies)
            .build();
    }
    
    /**
     * Statistics DTO for book copies
     */
    @lombok.Data
    @lombok.Builder
    public static class BookCopyStatistics {
        private Long bookId;
        private long totalCopies;
        private long availableCopies;
        private long borrowedCopies;
        private long reservedCopies;
        private long maintenanceCopies;
        
        public double getAvailabilityRate() {
            return totalCopies > 0 ? (double) availableCopies / totalCopies * 100 : 0.0;
        }
        
        public double getUtilizationRate() {
            return totalCopies > 0 ? (double) borrowedCopies / totalCopies * 100 : 0.0;
        }
    }
}
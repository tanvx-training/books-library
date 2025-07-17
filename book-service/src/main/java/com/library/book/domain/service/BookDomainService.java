package com.library.book.domain.service;

import com.library.book.domain.model.book.Book;
import com.library.book.domain.model.book.BookId;
import com.library.book.domain.model.bookcopy.BookCopy;
import com.library.book.domain.repository.BookCopyRepository;
import com.library.book.domain.repository.BookRepository;
import com.library.book.domain.specification.BookAvailabilitySpecification;
import com.library.book.domain.specification.BookSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Domain service for Book-related business logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookDomainService {
    
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    
    /**
     * Check if a book is available for borrowing
     */
    public boolean isBookAvailableForBorrowing(BookId bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            return false;
        }
        
        Book book = bookOpt.get();
        BookSpecification availabilitySpec = new BookAvailabilitySpecification(bookCopyRepository);
        
        return availabilitySpec.isSatisfiedBy(book);
    }
    
    /**
     * Get available copies count for a book
     */
    public long getAvailableCopiesCount(BookId bookId) {
        return bookCopyRepository.countAvailableCopiesByBookId(bookId);
    }
    
    /**
     * Get total copies count for a book
     */
    public long getTotalCopiesCount(BookId bookId) {
        return bookCopyRepository.countByBookId(bookId);
    }
    
    /**
     * Find the best available copy for borrowing
     */
    public Optional<BookCopy> findBestAvailableCopy(BookId bookId) {
        List<BookCopy> availableCopies = bookCopyRepository.findAvailableCopiesByBookId(bookId);
        
        if (availableCopies.isEmpty()) {
            return Optional.empty();
        }
        
        // Prioritize copies in better condition
        // Sort by condition (better condition first)
        // Then by copy number (lower number first)
        return availableCopies.stream()
            .filter(BookCopy::canBeBorrowed)
            .min(Comparator.comparing(BookCopy::getCondition).thenComparing(copy -> copy.getCopyNumber().getValue()));
    }
    
    /**
     * Check if a book can be safely deleted
     */
    public boolean canBookBeDeleted(BookId bookId) {
        List<BookCopy> copies = bookCopyRepository.findByBookId(bookId);
        
        // Book can be deleted if no copies are currently borrowed
        return copies.stream().noneMatch(copy -> 
            copy.getStatus().name().equals("BORROWED"));
    }
    
    /**
     * Get books that need attention (overdue copies, maintenance needed, etc.)
     */
    public List<BookCopy> getBooksNeedingAttention() {
        LocalDateTime now = LocalDateTime.now();
        return bookCopyRepository.findOverdueCopies(now);
    }
    
    /**
     * Get books due soon for reminder notifications
     */
    public List<BookCopy> getBooksDueSoon(int daysAhead) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(daysAhead);
        return bookCopyRepository.findCopiesDueSoon(now, futureDate);
    }
    
    /**
     * Calculate book popularity score based on borrowing history
     */
    public double calculateBookPopularityScore(BookId bookId) {
        // This is a simplified calculation
        // In a real system, you might consider:
        // - Number of times borrowed
        // - Recent borrowing frequency
        // - User ratings
        // - Reservation requests
        
        long totalCopies = getTotalCopiesCount(bookId);
        long availableCopies = getAvailableCopiesCount(bookId);
        
        if (totalCopies == 0) {
            return 0.0;
        }
        
        // Higher score means more popular (more copies are borrowed)
        double borrowedRatio = (double) (totalCopies - availableCopies) / totalCopies;
        return borrowedRatio * 100; // Convert to percentage
    }
    
    /**
     * Validate business rules for book operations
     */
    public void validateBookBusinessRules(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        
        if (book.getAuthorIds().isEmpty()) {
            throw new IllegalArgumentException("Book must have at least one author");
        }
        
        if (book.getCategoryIds().isEmpty()) {
            throw new IllegalArgumentException("Book must have at least one category");
        }
        
        // Add more business rules as needed
    }
} 
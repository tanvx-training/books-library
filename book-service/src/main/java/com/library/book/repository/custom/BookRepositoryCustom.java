package com.library.book.repository.custom;

import com.library.book.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Custom repository interface for advanced Book operations with logging
 */
public interface BookRepositoryCustom {
    
    /**
     * Find books by complex criteria with performance monitoring
     */
    List<Book> findBooksByComplexCriteria(String title, List<Long> authorIds, List<Long> categoryIds);
    
    /**
     * Find books with detailed information including all relations
     */
    Optional<Book> findBookWithDetails(Long bookId);
    
    /**
     * Advanced search with full-text capabilities
     */
    Page<Book> searchBooksAdvanced(String query, List<String> fields, Pageable pageable);
    
    /**
     * Database statistics and analytics operations
     */
    Long countBooksByCategory(Long categoryId);
    
    /**
     * Bulk operations with transaction management
     */
    int updateBookAvailability(List<Long> bookIds, boolean available);
} 
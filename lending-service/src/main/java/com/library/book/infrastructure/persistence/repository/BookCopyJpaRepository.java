package com.library.book.infrastructure.persistence.repository;

import com.library.book.infrastructure.persistence.entity.BookCopyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookCopyJpaRepository extends JpaRepository<BookCopyJpaEntity, Long> {
    
    /**
     * Find all book copies for a specific book
     */
    List<BookCopyJpaEntity> findByBookId(Long bookId);
    
    /**
     * Check if a copy number already exists for a specific book
     */
    boolean existsByBookIdAndCopyNumber(Long bookId, String copyNumber);
    
    /**
     * Count active borrowings for a book copy
     */
    @Query(value = 
        "SELECT COUNT(*) FROM borrowings b " +
        "WHERE b.book_copy_id = :bookCopyId " +
        "AND b.return_date IS NULL", 
        nativeQuery = true)
    long countActiveBookCopyBorrowings(@Param("bookCopyId") Long bookCopyId);
} 
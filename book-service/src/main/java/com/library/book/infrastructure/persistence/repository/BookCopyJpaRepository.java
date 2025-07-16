package com.library.book.infrastructure.persistence.repository;

import com.library.book.domain.model.bookcopy.BookCopyStatus;
import com.library.book.infrastructure.persistence.entity.BookCopyJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA Repository for BookCopy
 */
@Repository
public interface BookCopyJpaRepository extends JpaRepository<BookCopyJpaEntity, Long> {
    
    /**
     * Find all copies of a specific book
     */
    List<BookCopyJpaEntity> findByBookIdAndDeleteFlgFalse(Long bookId);
    
    /**
     * Find available copies of a specific book
     */
    @Query("SELECT bc FROM BookCopyJpaEntity bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'AVAILABLE' AND bc.deleteFlg = false")
    List<BookCopyJpaEntity> findAvailableCopiesByBookId(@Param("bookId") Long bookId);
    
    /**
     * Find copies by status
     */
    Page<BookCopyJpaEntity> findByStatusAndDeleteFlgFalse(BookCopyStatus status, Pageable pageable);
    
    /**
     * Find copies borrowed by a specific user
     */
    @Query("SELECT bc FROM BookCopyJpaEntity bc WHERE bc.currentBorrowerKeycloakId = :keycloakId " +
           "AND bc.status = 'BORROWED' AND bc.deleteFlg = false")
    List<BookCopyJpaEntity> findBorrowedByUser(@Param("keycloakId") String keycloakId);
    
    /**
     * Find overdue copies
     */
    @Query("SELECT bc FROM BookCopyJpaEntity bc WHERE bc.status = 'BORROWED' " +
           "AND bc.dueDate < :currentDate AND bc.deleteFlg = false")
    List<BookCopyJpaEntity> findOverdueCopies(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Find copies due soon
     */
    @Query("SELECT bc FROM BookCopyJpaEntity bc WHERE bc.status = 'BORROWED' " +
           "AND bc.dueDate BETWEEN :fromDate AND :toDate AND bc.deleteFlg = false")
    List<BookCopyJpaEntity> findCopiesDueSoon(
        @Param("fromDate") LocalDateTime fromDate, 
        @Param("toDate") LocalDateTime toDate);
    
    /**
     * Check if copy number exists for a book
     */
    boolean existsByBookIdAndCopyNumberAndDeleteFlgFalse(Long bookId, String copyNumber);
    
    /**
     * Count available copies for a book
     */
    @Query("SELECT COUNT(bc) FROM BookCopyJpaEntity bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'AVAILABLE' AND bc.deleteFlg = false")
    long countAvailableCopiesByBookId(@Param("bookId") Long bookId);
    
    /**
     * Count total copies for a book
     */
    long countByBookIdAndDeleteFlgFalse(Long bookId);
    
    /**
     * Find all with pagination (excluding deleted)
     */
    Page<BookCopyJpaEntity> findByDeleteFlgFalse(Pageable pageable);
}
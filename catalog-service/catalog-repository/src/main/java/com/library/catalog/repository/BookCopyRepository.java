package com.library.catalog.repository;

import com.library.catalog.repository.entity.BookCopy;
import com.library.catalog.repository.enums.BookCopyCondition;
import com.library.catalog.repository.enums.BookCopyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    /**
     * Find book copy by public ID, excluding soft-deleted records
     */
    Optional<BookCopy> findByPublicIdAndDeletedAtIsNull(UUID publicId);

    /**
     * Check if book copy exists by public ID, excluding soft-deleted records
     */
    boolean existsByPublicIdAndDeletedAtIsNull(UUID publicId);

    /**
     * Find all book copies for a specific book, excluding soft-deleted records
     */
    List<BookCopy> findByBookIdAndDeletedAtIsNull(Long bookId);

    /**
     * Find all book copies for a specific book with pagination, excluding soft-deleted records
     */
    Page<BookCopy> findByBookIdAndDeletedAtIsNull(Long bookId, Pageable pageable);

    /**
     * Find book copy by book ID and copy number, excluding soft-deleted records
     */
    Optional<BookCopy> findByBookIdAndCopyNumberAndDeletedAtIsNull(Long bookId, String copyNumber);

    /**
     * Check if book copy exists by book ID and copy number, excluding soft-deleted records
     */
    boolean existsByBookIdAndCopyNumberAndDeletedAtIsNull(Long bookId, String copyNumber);

    /**
     * Check if book copy exists by book ID and copy number excluding a specific copy (for updates)
     */
    boolean existsByBookIdAndCopyNumberAndDeletedAtIsNullAndPublicIdNot(Long bookId, String copyNumber, UUID publicId);

    /**
     * Find all book copies excluding soft-deleted records
     */
    Page<BookCopy> findByDeletedAtIsNull(Pageable pageable);

    /**
     * Find book copies by status, excluding soft-deleted records
     */
    List<BookCopy> findByStatusAndDeletedAtIsNull(BookCopyStatus status);

    /**
     * Find book copies by status with pagination, excluding soft-deleted records
     */
    Page<BookCopy> findByStatusAndDeletedAtIsNull(BookCopyStatus status, Pageable pageable);

    /**
     * Find book copies by condition, excluding soft-deleted records
     */
    List<BookCopy> findByConditionAndDeletedAtIsNull(BookCopyCondition condition);

    /**
     * Find book copies by condition with pagination, excluding soft-deleted records
     */
    Page<BookCopy> findByConditionAndDeletedAtIsNull(BookCopyCondition condition, Pageable pageable);

    /**
     * Find book copies by location, excluding soft-deleted records
     */
    List<BookCopy> findByLocationAndDeletedAtIsNull(String location);

    /**
     * Find book copies by location with pagination, excluding soft-deleted records
     */
    Page<BookCopy> findByLocationAndDeletedAtIsNull(String location, Pageable pageable);

    /**
     * Find available book copies for a specific book
     */
    @Query("SELECT bc FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'AVAILABLE' AND bc.deletedAt IS NULL")
    List<BookCopy> findAvailableCopiesByBookId(@Param("bookId") Long bookId);

    /**
     * Find borrowed book copies for a specific book
     */
    @Query("SELECT bc FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'BORROWED' AND bc.deletedAt IS NULL")
    List<BookCopy> findBorrowedCopiesByBookId(@Param("bookId") Long bookId);

    /**
     * Find reserved book copies for a specific book
     */
    @Query("SELECT bc FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'RESERVED' AND bc.deletedAt IS NULL")
    List<BookCopy> findReservedCopiesByBookId(@Param("bookId") Long bookId);

    /**
     * Count total copies for a specific book, excluding soft-deleted records
     */
    long countByBookIdAndDeletedAtIsNull(Long bookId);

    /**
     * Count available copies for a specific book
     */
    @Query("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'AVAILABLE' AND bc.deletedAt IS NULL")
    long countAvailableCopiesByBookId(@Param("bookId") Long bookId);

    /**
     * Count borrowed copies for a specific book
     */
    @Query("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'BORROWED' AND bc.deletedAt IS NULL")
    long countBorrowedCopiesByBookId(@Param("bookId") Long bookId);

    /**
     * Count reserved copies for a specific book
     */
    @Query("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'RESERVED' AND bc.deletedAt IS NULL")
    long countReservedCopiesByBookId(@Param("bookId") Long bookId);

    /**
     * Find book copies that can be borrowed (available and not damaged)
     */
    @Query("SELECT bc FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'AVAILABLE' AND bc.condition != 'DAMAGED' " +
           "AND bc.deletedAt IS NULL")
    List<BookCopy> findBorrowableCopiesByBookId(@Param("bookId") Long bookId);

    /**
     * Count book copies that can be borrowed (available and not damaged)
     */
    @Query("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'AVAILABLE' AND bc.condition != 'DAMAGED' " +
           "AND bc.deletedAt IS NULL")
    long countBorrowableCopiesByBookId(@Param("bookId") Long bookId);

    /**
     * Search book copies with comprehensive filtering
     */
    @Query("SELECT bc FROM BookCopy bc " +
           "LEFT JOIN Book b ON bc.bookId = b.id " +
           "WHERE bc.deletedAt IS NULL " +
           "AND (:bookId IS NULL OR bc.bookId = :bookId) " +
           "AND (:copyNumber IS NULL OR LOWER(bc.copyNumber) LIKE LOWER(CONCAT('%', :copyNumber, '%'))) " +
           "AND (:status IS NULL OR bc.status = :status) " +
           "AND (:condition IS NULL OR bc.condition = :condition) " +
           "AND (:location IS NULL OR LOWER(bc.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND (b.deletedAt IS NULL)")
    Page<BookCopy> searchBookCopies(@Param("bookId") Long bookId,
                                   @Param("copyNumber") String copyNumber,
                                   @Param("status") BookCopyStatus status,
                                   @Param("condition") BookCopyCondition condition,
                                   @Param("location") String location,
                                   Pageable pageable);

    /**
     * Find book copies by multiple book IDs, excluding soft-deleted records
     */
    List<BookCopy> findByBookIdInAndDeletedAtIsNull(List<Long> bookIds);

    /**
     * Check if any book copies exist for a book (used for deletion validation)
     */
    boolean existsByBookIdAndDeletedAtIsNull(Long bookId);

    /**
     * Find the next available copy number for a book
     */
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(bc.copyNumber, 2) AS int)), 0) + 1 " +
           "FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.copyNumber LIKE 'C%' AND bc.deletedAt IS NULL")
    Integer findNextCopyNumber(@Param("bookId") Long bookId);

    /**
     * Find book copies by book public ID through book relationship
     */
    @Query("SELECT bc FROM BookCopy bc " +
           "JOIN Book b ON bc.bookId = b.id " +
           "WHERE b.publicId = :bookPublicId " +
           "AND bc.deletedAt IS NULL " +
           "AND b.deletedAt IS NULL")
    List<BookCopy> findByBookPublicId(@Param("bookPublicId") UUID bookPublicId);

    /**
     * Count book copies by book public ID through book relationship
     */
    @Query("SELECT COUNT(bc) FROM BookCopy bc " +
           "JOIN Book b ON bc.bookId = b.id " +
           "WHERE b.publicId = :bookPublicId " +
           "AND bc.deletedAt IS NULL " +
           "AND b.deletedAt IS NULL")
    long countByBookPublicId(@Param("bookPublicId") UUID bookPublicId);
}
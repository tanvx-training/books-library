package com.library.catalog.repository;

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

    Optional<BookCopy> findByPublicIdAndDeletedAtIsNull(UUID publicId);

    boolean existsByPublicIdAndDeletedAtIsNull(UUID publicId);

    List<BookCopy> findByBookIdAndDeletedAtIsNull(Long bookId);

    Page<BookCopy> findByBookIdAndDeletedAtIsNull(Long bookId, Pageable pageable);

    Optional<BookCopy> findByBookIdAndCopyNumberAndDeletedAtIsNull(Long bookId, String copyNumber);

    boolean existsByBookIdAndCopyNumberAndDeletedAtIsNull(Long bookId, String copyNumber);

    boolean existsByBookIdAndCopyNumberAndDeletedAtIsNullAndPublicIdNot(Long bookId, String copyNumber, UUID publicId);

    Page<BookCopy> findByDeletedAtIsNull(Pageable pageable);

    List<BookCopy> findByStatusAndDeletedAtIsNull(BookCopyStatus status);

    Page<BookCopy> findByStatusAndDeletedAtIsNull(BookCopyStatus status, Pageable pageable);

    List<BookCopy> findByConditionAndDeletedAtIsNull(BookCopyCondition condition);

    Page<BookCopy> findByConditionAndDeletedAtIsNull(BookCopyCondition condition, Pageable pageable);

    List<BookCopy> findByLocationAndDeletedAtIsNull(String location);

    Page<BookCopy> findByLocationAndDeletedAtIsNull(String location, Pageable pageable);

    @Query("SELECT bc FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'AVAILABLE' AND bc.deletedAt IS NULL")
    List<BookCopy> findAvailableCopiesByBookId(@Param("bookId") Long bookId);

    @Query("SELECT bc FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'BORROWED' AND bc.deletedAt IS NULL")
    List<BookCopy> findBorrowedCopiesByBookId(@Param("bookId") Long bookId);

    @Query("SELECT bc FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'RESERVED' AND bc.deletedAt IS NULL")
    List<BookCopy> findReservedCopiesByBookId(@Param("bookId") Long bookId);

    long countByBookIdAndDeletedAtIsNull(Long bookId);

    @Query("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'AVAILABLE' AND bc.deletedAt IS NULL")
    long countAvailableCopiesByBookId(@Param("bookId") Long bookId);

    @Query("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'BORROWED' AND bc.deletedAt IS NULL")
    long countBorrowedCopiesByBookId(@Param("bookId") Long bookId);

    @Query("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'RESERVED' AND bc.deletedAt IS NULL")
    long countReservedCopiesByBookId(@Param("bookId") Long bookId);

    @Query("SELECT bc FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'AVAILABLE' AND bc.condition != 'DAMAGED' " +
           "AND bc.deletedAt IS NULL")
    List<BookCopy> findBorrowableCopiesByBookId(@Param("bookId") Long bookId);

    @Query("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.status = 'AVAILABLE' AND bc.condition != 'DAMAGED' " +
           "AND bc.deletedAt IS NULL")
    long countBorrowableCopiesByBookId(@Param("bookId") Long bookId);

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

    List<BookCopy> findByBookIdInAndDeletedAtIsNull(List<Long> bookIds);

    boolean existsByBookIdAndDeletedAtIsNull(Long bookId);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(bc.copyNumber, 2) AS int)), 0) + 1 " +
           "FROM BookCopy bc WHERE bc.bookId = :bookId " +
           "AND bc.copyNumber LIKE 'C%' AND bc.deletedAt IS NULL")
    Integer findNextCopyNumber(@Param("bookId") Long bookId);

    @Query("SELECT bc FROM BookCopy bc " +
           "JOIN Book b ON bc.bookId = b.id " +
           "WHERE b.publicId = :bookPublicId " +
           "AND bc.deletedAt IS NULL " +
           "AND b.deletedAt IS NULL")
    List<BookCopy> findByBookPublicId(@Param("bookPublicId") UUID bookPublicId);

    @Query("SELECT COUNT(bc) FROM BookCopy bc " +
           "JOIN Book b ON bc.bookId = b.id " +
           "WHERE b.publicId = :bookPublicId " +
           "AND bc.deletedAt IS NULL " +
           "AND b.deletedAt IS NULL")
    long countByBookPublicId(@Param("bookPublicId") UUID bookPublicId);
}
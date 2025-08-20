package com.library.loan.repository;

import com.library.loan.repository.Borrowing;
import com.library.loan.repository.BorrowingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long>, JpaSpecificationExecutor<Borrowing> {

    // Enhanced query methods for filtering and searching
    Optional<Borrowing> findByPublicIdAndDeletedAtIsNull(UUID publicId);

    Page<Borrowing> findByDeletedAtIsNull(Pageable pageable);

    List<Borrowing> findByUserIdAndStatus(Long userId, BorrowingStatus status);

    Page<Borrowing> findByUserId(Long userId, Pageable pageable);

    List<Borrowing> findByBookCopyId(Long bookCopyId);

    // Custom query methods for advanced filtering
    @Query("SELECT b FROM Borrowing b WHERE b.deletedAt IS NULL AND " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:borrowDateFrom IS NULL OR b.borrowDate >= :borrowDateFrom) AND " +
           "(:borrowDateTo IS NULL OR b.borrowDate <= :borrowDateTo) AND " +
           "(:dueDateFrom IS NULL OR b.dueDate >= :dueDateFrom) AND " +
           "(:dueDateTo IS NULL OR b.dueDate <= :dueDateTo)")
    Page<Borrowing> findBorrowingsWithFilters(
            @Param("status") BorrowingStatus status,
            @Param("borrowDateFrom") LocalDate borrowDateFrom,
            @Param("borrowDateTo") LocalDate borrowDateTo,
            @Param("dueDateFrom") LocalDate dueDateFrom,
            @Param("dueDateTo") LocalDate dueDateTo,
            Pageable pageable);

    @Query("SELECT b FROM Borrowing b WHERE b.dueDate < :date AND b.status = :status")
    List<Borrowing> findOverdueBorrowings(@Param("date") LocalDate date, @Param("status") BorrowingStatus status);

    @Query("SELECT COUNT(b) FROM Borrowing b WHERE b.userId = :userId AND b.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") BorrowingStatus status);

    @Query("SELECT b FROM Borrowing b WHERE b.deletedAt IS NULL")
    List<Borrowing> findAllActive();

    @Query("SELECT b FROM Borrowing b WHERE b.userId = :userId AND b.deletedAt IS NULL")
    Page<Borrowing> findActiveByUserId(@Param("userId") Long userId, Pageable pageable);

    // Additional query methods for business logic
    @Query("SELECT COUNT(b) FROM Borrowing b WHERE b.userId = :userId AND b.status IN :statuses AND b.deletedAt IS NULL")
    long countActiveByUserIdAndStatuses(@Param("userId") Long userId, @Param("statuses") List<BorrowingStatus> statuses);

    @Query("SELECT b FROM Borrowing b WHERE b.bookCopyId = :bookCopyId AND b.status IN :statuses AND b.deletedAt IS NULL")
    List<Borrowing> findActiveByBookCopyIdAndStatuses(@Param("bookCopyId") Long bookCopyId, @Param("statuses") List<BorrowingStatus> statuses);

    @Query("SELECT b FROM Borrowing b WHERE b.status = :status AND b.dueDate BETWEEN :startDate AND :endDate AND b.deletedAt IS NULL")
    List<Borrowing> findByStatusAndDueDateBetween(@Param("status") BorrowingStatus status, 
                                                  @Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate);

    // Additional methods for comprehensive borrowing management
    @Query("SELECT b FROM Borrowing b WHERE b.publicId = :publicId AND b.deletedAt IS NULL")
    Optional<Borrowing> findActiveByPublicId(@Param("publicId") UUID publicId);

    @Query("SELECT b FROM Borrowing b WHERE b.bookCopyId = :bookCopyId AND b.status = :status AND b.deletedAt IS NULL")
    Optional<Borrowing> findByBookCopyIdAndStatus(@Param("bookCopyId") Long bookCopyId, @Param("status") BorrowingStatus status);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Borrowing b WHERE b.bookCopyId = :bookCopyId AND b.status IN :activeStatuses AND b.deletedAt IS NULL")
    boolean existsActiveByBookCopyId(@Param("bookCopyId") Long bookCopyId, @Param("activeStatuses") List<BorrowingStatus> activeStatuses);

    @Query("SELECT b FROM Borrowing b WHERE b.userId = :userId AND b.status IN :statuses AND b.deletedAt IS NULL ORDER BY b.borrowDate DESC")
    List<Borrowing> findByUserIdAndStatusesOrderByBorrowDateDesc(@Param("userId") Long userId, @Param("statuses") List<BorrowingStatus> statuses);

    @Query("SELECT b FROM Borrowing b WHERE b.dueDate <= :date AND b.status = :status AND b.deletedAt IS NULL ORDER BY b.dueDate ASC")
    List<Borrowing> findDueSoonBorrowings(@Param("date") LocalDate date, @Param("status") BorrowingStatus status);
}
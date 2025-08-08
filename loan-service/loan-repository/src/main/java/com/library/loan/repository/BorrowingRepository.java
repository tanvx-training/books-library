package com.library.loan.repository;

import com.library.loan.repository.entity.Borrowing;
import com.library.loan.repository.enums.BorrowingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    Optional<Borrowing> findByPublicId(UUID publicId);

    List<Borrowing> findByUserIdAndStatus(Long userId, BorrowingStatus status);

    Page<Borrowing> findByUserId(Long userId, Pageable pageable);

    List<Borrowing> findByBookCopyId(Long bookCopyId);

    @Query("SELECT b FROM Borrowing b WHERE b.dueDate < :date AND b.status = :status")
    List<Borrowing> findOverdueBorrowings(@Param("date") LocalDate date, @Param("status") BorrowingStatus status);

    @Query("SELECT COUNT(b) FROM Borrowing b WHERE b.userId = :userId AND b.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") BorrowingStatus status);

    @Query("SELECT b FROM Borrowing b WHERE b.deletedAt IS NULL")
    List<Borrowing> findAllActive();

    @Query("SELECT b FROM Borrowing b WHERE b.userId = :userId AND b.deletedAt IS NULL")
    Page<Borrowing> findActiveByUserId(@Param("userId") Long userId, Pageable pageable);
}
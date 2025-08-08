package com.library.loan.repository;

import com.library.loan.repository.entity.Fine;
import com.library.loan.repository.enums.FineStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    Optional<Fine> findByPublicId(UUID publicId);

    List<Fine> findByBorrowingId(Long borrowingId);

    @Query("SELECT f FROM Fine f JOIN Borrowing b ON f.borrowingId = b.id WHERE b.userId = :userId AND f.status = :status")
    List<Fine> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") FineStatus status);

    @Query("SELECT f FROM Fine f JOIN Borrowing b ON f.borrowingId = b.id WHERE b.userId = :userId")
    Page<Fine> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT SUM(f.amount) FROM Fine f JOIN Borrowing b ON f.borrowingId = b.id WHERE b.userId = :userId AND f.status = :status")
    BigDecimal getTotalFineAmountByUserIdAndStatus(@Param("userId") Long userId, @Param("status") FineStatus status);

    @Query("SELECT f FROM Fine f WHERE f.status = :status")
    List<Fine> findByStatus(@Param("status") FineStatus status);

    @Query("SELECT f FROM Fine f WHERE f.deletedAt IS NULL")
    List<Fine> findAllActive();
}
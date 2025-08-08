package com.library.loan.repository;

import com.library.loan.repository.entity.Reservation;
import com.library.loan.repository.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByPublicId(UUID publicId);

    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);

    Page<Reservation> findByUserId(Long userId, Pageable pageable);

    List<Reservation> findByBookIdAndStatus(Long bookId, ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.expiryDate < :date AND r.status = :status")
    List<Reservation> findExpiredReservations(@Param("date") LocalDateTime date, @Param("status") ReservationStatus status);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.userId = :userId AND r.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.bookId = :bookId AND r.status = :status ORDER BY r.reservationDate ASC")
    List<Reservation> findByBookIdAndStatusOrderByReservationDate(@Param("bookId") Long bookId, @Param("status") ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.deletedAt IS NULL")
    List<Reservation> findAllActive();

    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId AND r.deletedAt IS NULL")
    Page<Reservation> findActiveByUserId(@Param("userId") Long userId, Pageable pageable);
}
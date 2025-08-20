package com.library.loan.repository;

import com.library.loan.repository.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    // Basic CRUD operations
    Optional<Reservation> findByPublicIdAndDeletedAtIsNull(UUID publicId);

    Page<Reservation> findByDeletedAtIsNull(Pageable pageable);
}
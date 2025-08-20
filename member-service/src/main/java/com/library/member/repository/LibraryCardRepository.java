package com.library.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LibraryCardRepository extends JpaRepository<LibraryCardEntity, Long> {

    // Find all non-deleted library cards with pagination
    Page<LibraryCardEntity> findByDeletedAtIsNull(Pageable pageable);

    // Find library cards by status excluding deleted ones
    Page<LibraryCardEntity> findByStatusAndDeletedAtIsNull(LibraryCardStatus status, Pageable pageable);

    // Find library cards by user_id excluding deleted ones
    List<LibraryCardEntity> findByUserIdAndDeletedAtIsNull(Long userId);

    // Find library cards by user's public_id excluding deleted ones
    @Query("SELECT lc FROM LibraryCardEntity lc " +
           "JOIN UserEntity u ON lc.userId = u.id " +
           "WHERE u.publicId = :userPublicId " +
           "AND lc.deletedAt IS NULL")
    List<LibraryCardEntity> findByUserPublicIdAndDeletedAtIsNull(@Param("userPublicId") UUID userPublicId);

    // Find library cards by user's keycloak_id excluding deleted ones
    @Query("SELECT lc FROM LibraryCardEntity lc " +
           "JOIN UserEntity u ON lc.userId = u.id " +
           "WHERE u.keycloakId = :keycloakId " +
           "AND lc.deletedAt IS NULL")
    List<LibraryCardEntity> findByUserKeycloakIdAndDeletedAtIsNull(@Param("keycloakId") String keycloakId);

    // Find active library cards by user_id
    @Query("SELECT lc FROM LibraryCardEntity lc " +
           "WHERE lc.userId = :userId " +
           "AND lc.status = 'ACTIVE' " +
           "AND lc.deletedAt IS NULL")
    List<LibraryCardEntity> findActiveCardsByUserId(@Param("userId") Long userId);

    // Find active library cards by user's public_id
    @Query("SELECT lc FROM LibraryCardEntity lc " +
           "JOIN UserEntity u ON lc.userId = u.id " +
           "WHERE u.publicId = :userPublicId " +
           "AND lc.status = 'ACTIVE' " +
           "AND lc.deletedAt IS NULL")
    List<LibraryCardEntity> findActiveCardsByUserPublicId(@Param("userPublicId") UUID userPublicId);

    // Find library card by public_id excluding deleted ones
    Optional<LibraryCardEntity> findByPublicIdAndDeletedAtIsNull(UUID publicId);

    // Find library card by card number excluding deleted ones
    Optional<LibraryCardEntity> findByCardNumberAndDeletedAtIsNull(String cardNumber);

    // Check if library card exists by public_id excluding deleted ones
    boolean existsByPublicIdAndDeletedAtIsNull(UUID publicId);

    // Check if library card exists by card number excluding deleted ones
    boolean existsByCardNumberAndDeletedAtIsNull(String cardNumber);

    // Find library cards expiring before a certain date
    @Query("SELECT lc FROM LibraryCardEntity lc " +
           "WHERE lc.expiryDate <= :expiryDate " +
           "AND lc.status = 'ACTIVE' " +
           "AND lc.deletedAt IS NULL")
    List<LibraryCardEntity> findExpiringCards(@Param("expiryDate") LocalDate expiryDate);

    // Find library cards expiring within a date range
    @Query("SELECT lc FROM LibraryCardEntity lc " +
           "WHERE lc.expiryDate BETWEEN :startDate AND :endDate " +
           "AND lc.status = 'ACTIVE' " +
           "AND lc.deletedAt IS NULL")
    List<LibraryCardEntity> findCardsExpiringBetween(@Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);

    // Find expired library cards that are still marked as active
    @Query("SELECT lc FROM LibraryCardEntity lc " +
           "WHERE lc.expiryDate < CURRENT_DATE " +
           "AND lc.status = 'ACTIVE' " +
           "AND lc.deletedAt IS NULL")
    List<LibraryCardEntity> findExpiredActiveCards();

    // Find library cards by status and user role
    @Query("SELECT lc FROM LibraryCardEntity lc " +
           "JOIN UserEntity u ON lc.userId = u.id " +
           "WHERE lc.status = :status " +
           "AND u.role = :userRole " +
           "AND lc.deletedAt IS NULL")
    Page<LibraryCardEntity> findByStatusAndUserRole(@Param("status") LibraryCardStatus status, 
                                                   @Param("userRole") com.library.member.repository.UserRole userRole, 
                                                   Pageable pageable);

    // Find library cards issued within a date range
    @Query("SELECT lc FROM LibraryCardEntity lc " +
           "WHERE lc.issueDate BETWEEN :startDate AND :endDate " +
           "AND lc.deletedAt IS NULL")
    Page<LibraryCardEntity> findCardsIssuedBetween(@Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate, 
                                                  Pageable pageable);

    // Count active library cards for a user by user_id
    @Query("SELECT COUNT(lc) FROM LibraryCardEntity lc " +
           "WHERE lc.userId = :userId " +
           "AND lc.status = 'ACTIVE' " +
           "AND lc.deletedAt IS NULL")
    long countActiveCardsByUserId(@Param("userId") Long userId);

    // Count active library cards for a user by public_id
    @Query("SELECT COUNT(lc) FROM LibraryCardEntity lc " +
           "JOIN UserEntity u ON lc.userId = u.id " +
           "WHERE u.publicId = :userPublicId " +
           "AND lc.status = 'ACTIVE' " +
           "AND lc.deletedAt IS NULL")
    long countActiveCardsByUserPublicId(@Param("userPublicId") UUID userPublicId);

    // Custom soft delete method using @Modifying query
    @Modifying
    @Query("UPDATE LibraryCardEntity lc SET lc.deletedAt = :deletedAt, lc.updatedAt = :updatedAt, lc.updatedBy = :updatedBy " +
           "WHERE lc.publicId = :publicId AND lc.deletedAt IS NULL")
    void softDeleteByPublicId(@Param("publicId") UUID publicId,
                            @Param("deletedAt") LocalDateTime deletedAt,
                            @Param("updatedAt") LocalDateTime updatedAt,
                            @Param("updatedBy") String updatedBy);

    // Update library card status
    @Modifying
    @Query("UPDATE LibraryCardEntity lc SET lc.status = :status, lc.updatedAt = :updatedAt, lc.updatedBy = :updatedBy " +
           "WHERE lc.publicId = :publicId AND lc.deletedAt IS NULL")
    void updateStatusByPublicId(@Param("publicId") UUID publicId,
                              @Param("status") LibraryCardStatus status,
                              @Param("updatedAt") LocalDateTime updatedAt,
                              @Param("updatedBy") String updatedBy);

    // Update library card expiry date
    @Modifying
    @Query("UPDATE LibraryCardEntity lc SET lc.expiryDate = :expiryDate, lc.updatedAt = :updatedAt, lc.updatedBy = :updatedBy " +
           "WHERE lc.publicId = :publicId AND lc.deletedAt IS NULL")
    void updateExpiryDateByPublicId(@Param("publicId") UUID publicId,
                                  @Param("expiryDate") LocalDate expiryDate,
                                  @Param("updatedAt") LocalDateTime updatedAt,
                                  @Param("updatedBy") String updatedBy);

    // Bulk update expired cards status
    @Modifying
    @Query("UPDATE LibraryCardEntity lc SET lc.status = 'EXPIRED', lc.updatedAt = :updatedAt, lc.updatedBy = :updatedBy " +
           "WHERE lc.expiryDate < CURRENT_DATE " +
           "AND lc.status = 'ACTIVE' " +
           "AND lc.deletedAt IS NULL")
    int markExpiredCardsAsExpired(@Param("updatedAt") LocalDateTime updatedAt,
                                @Param("updatedBy") String updatedBy);
}
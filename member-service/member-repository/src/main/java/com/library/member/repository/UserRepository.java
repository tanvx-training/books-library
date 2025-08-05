package com.library.member.repository;

import com.library.member.repository.entity.UserEntity;
import com.library.member.repository.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Find all non-deleted users with pagination
    Page<UserEntity> findByDeletedAtIsNull(Pageable pageable);

    // Find users by role excluding deleted ones
    Page<UserEntity> findByRoleAndDeletedAtIsNull(UserRole role, Pageable pageable);

    // Find active users excluding deleted ones
    Page<UserEntity> findByIsActiveAndDeletedAtIsNull(Boolean isActive, Pageable pageable);

    // Find users by role and active status excluding deleted ones
    Page<UserEntity> findByRoleAndIsActiveAndDeletedAtIsNull(UserRole role, Boolean isActive, Pageable pageable);

    // Search users by name (case-insensitive) excluding deleted ones
    @Query("SELECT u FROM UserEntity u WHERE " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND u.deletedAt IS NULL")
    Page<UserEntity> findByNameContaining(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Search users by email (case-insensitive) excluding deleted ones
    Page<UserEntity> findByEmailContainingIgnoreCaseAndDeletedAtIsNull(String email, Pageable pageable);

    // Find user by public_id excluding deleted ones
    Optional<UserEntity> findByPublicIdAndDeletedAtIsNull(UUID publicId);

    // Find user by keycloak_id excluding deleted ones
    Optional<UserEntity> findByKeycloakIdAndDeletedAtIsNull(String keycloakId);

    // Find user by email excluding deleted ones
    Optional<UserEntity> findByEmailAndDeletedAtIsNull(String email);

    // Find user by username excluding deleted ones
    Optional<UserEntity> findByUsernameAndDeletedAtIsNull(String username);

    // Check if user exists by public_id excluding deleted ones
    boolean existsByPublicIdAndDeletedAtIsNull(UUID publicId);

    // Check if user exists by keycloak_id excluding deleted ones
    boolean existsByKeycloakIdAndDeletedAtIsNull(String keycloakId);

    // Check if user exists by email excluding deleted ones
    boolean existsByEmailAndDeletedAtIsNull(String email);

    // Check if user exists by username excluding deleted ones
    boolean existsByUsernameAndDeletedAtIsNull(String username);

    // Find users by keycloak_id (including deleted ones for sync purposes)
    Optional<UserEntity> findByKeycloakId(String keycloakId);

    // Find all users by role (including deleted ones for admin purposes)
    List<UserEntity> findByRole(UserRole role);

    // Find users with expiring library cards
    @Query("SELECT DISTINCT u FROM UserEntity u " +
           "WHERE u.deletedAt IS NULL " +
           "AND EXISTS (" +
           "    SELECT lc FROM LibraryCardEntity lc " +
           "    WHERE lc.userId = u.id " +
           "    AND lc.deletedAt IS NULL " +
           "    AND lc.expiryDate <= :expiryThreshold " +
           "    AND lc.status = 'ACTIVE'" +
           ")")
    List<UserEntity> findUsersWithExpiringLibraryCards(@Param("expiryThreshold") java.time.LocalDate expiryThreshold);

    // Find users without active library cards
    @Query("SELECT u FROM UserEntity u " +
           "WHERE u.deletedAt IS NULL " +
           "AND u.isActive = true " +
           "AND NOT EXISTS (" +
           "    SELECT lc FROM LibraryCardEntity lc " +
           "    WHERE lc.userId = u.id " +
           "    AND lc.deletedAt IS NULL " +
           "    AND lc.status = 'ACTIVE'" +
           ")")
    List<UserEntity> findUsersWithoutActiveLibraryCards();

    // Custom soft delete method using @Modifying query
    @Modifying
    @Query("UPDATE UserEntity u SET u.deletedAt = :deletedAt, u.updatedAt = :updatedAt, u.updatedBy = :updatedBy " +
           "WHERE u.publicId = :publicId AND u.deletedAt IS NULL")
    void softDeleteByPublicId(@Param("publicId") UUID publicId,
                            @Param("deletedAt") LocalDateTime deletedAt,
                            @Param("updatedAt") LocalDateTime updatedAt,
                            @Param("updatedBy") String updatedBy);

    // Custom soft delete method by keycloak_id
    @Modifying
    @Query("UPDATE UserEntity u SET u.deletedAt = :deletedAt, u.updatedAt = :updatedAt, u.updatedBy = :updatedBy " +
           "WHERE u.keycloakId = :keycloakId AND u.deletedAt IS NULL")
    void softDeleteByKeycloakId(@Param("keycloakId") String keycloakId,
                              @Param("deletedAt") LocalDateTime deletedAt,
                              @Param("updatedAt") LocalDateTime updatedAt,
                              @Param("updatedBy") String updatedBy);

    // Update user active status
    @Modifying
    @Query("UPDATE UserEntity u SET u.isActive = :isActive, u.updatedAt = :updatedAt, u.updatedBy = :updatedBy " +
           "WHERE u.publicId = :publicId AND u.deletedAt IS NULL")
    void updateActiveStatusByPublicId(@Param("publicId") UUID publicId,
                                    @Param("isActive") Boolean isActive,
                                    @Param("updatedAt") LocalDateTime updatedAt,
                                    @Param("updatedBy") String updatedBy);

    // Update user role
    @Modifying
    @Query("UPDATE UserEntity u SET u.role = :role, u.updatedAt = :updatedAt, u.updatedBy = :updatedBy " +
           "WHERE u.publicId = :publicId AND u.deletedAt IS NULL")
    void updateRoleByPublicId(@Param("publicId") UUID publicId,
                            @Param("role") UserRole role,
                            @Param("updatedAt") LocalDateTime updatedAt,
                            @Param("updatedBy") String updatedBy);
}
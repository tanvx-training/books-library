package com.library.catalog.repository;

import com.library.catalog.repository.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // Find all non-deleted authors with pagination
    Page<Author> findByDeletedAtIsNull(Pageable pageable);

    // Search authors by name (case-insensitive) excluding deleted ones
    Page<Author> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String name, Pageable pageable);

    // Find author by public_id excluding deleted ones
    Optional<Author> findByPublicIdAndDeletedAtIsNull(UUID publicId);

    // Check if author exists by public_id excluding deleted ones
    boolean existsByPublicIdAndDeletedAtIsNull(UUID publicId);

    // Check if author exists by name (case-insensitive) excluding deleted ones
    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);

    // Find author by name (case-insensitive) excluding deleted ones
    Optional<Author> findByNameIgnoreCaseAndDeletedAtIsNull(String name);

    // Custom soft delete method using @Modifying query
    @Modifying
    @Query("UPDATE Author a SET a.deletedAt = :deletedAt, a.updatedAt = :updatedAt, a.updatedBy = :updatedBy WHERE a.publicId = :publicId AND a.deletedAt IS NULL")
    void softDeleteByPublicId(@Param("publicId") UUID publicId,
                            @Param("deletedAt") LocalDateTime deletedAt,
                            @Param("updatedAt") LocalDateTime updatedAt, 
                            @Param("updatedBy") String updatedBy);
}
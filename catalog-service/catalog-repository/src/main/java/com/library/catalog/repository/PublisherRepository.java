package com.library.catalog.repository;

import com.library.catalog.repository.entity.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    Optional<Publisher> findByIdAndDeletedAtIsNull(Long id);

    // Find all non-deleted publishers with pagination
    Page<Publisher> findByDeletedAtIsNull(Pageable pageable);

    // Search publishers by name (case-insensitive) excluding deleted ones
    Page<Publisher> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String name, Pageable pageable);

    // Find publisher by public_id excluding deleted ones
    Optional<Publisher> findByPublicIdAndDeletedAtIsNull(UUID publicId);

    // Check if publisher exists by public_id excluding deleted ones
    boolean existsByPublicIdAndDeletedAtIsNull(UUID publicId);

    // Check if publisher exists by name (case-insensitive) excluding deleted ones
    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);

    // Find publisher by name (case-insensitive) excluding deleted ones
    Optional<Publisher> findByNameIgnoreCaseAndDeletedAtIsNull(String name);

    // Check if publisher exists by internal ID excluding deleted ones (for internal validation)
    boolean existsByIdAndDeletedAtIsNull(Long id);

    // Find publisher internal ID by public ID (for public_id to internal ID resolution)
    @Query("SELECT p.id FROM Publisher p WHERE p.publicId = :publicId AND p.deletedAt IS NULL")
    Optional<Long> findPublisherIdByPublicId(@Param("publicId") UUID publicId);
}
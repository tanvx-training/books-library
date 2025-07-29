package com.library.catalog.repository;

import com.library.catalog.repository.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find active categories with pagination
    Page<Category> findByDeletedAtIsNull(Pageable pageable);

    // Find active category by public ID
    Optional<Category> findByPublicIdAndDeletedAtIsNull(UUID publicId);

    // Check if active category exists by public ID
    boolean existsByPublicIdAndDeletedAtIsNull(UUID publicId);

    // Check if active category exists by name (case-insensitive)
    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);

    // Check if active category exists by slug (case-insensitive)
    boolean existsBySlugIgnoreCaseAndDeletedAtIsNull(String slug);

    // Check if category with name exists excluding specific ID (for update validation)
    boolean existsByNameIgnoreCaseAndDeletedAtIsNullAndIdNot(String name, Long id);

    // Check if category with slug exists excluding specific ID (for update validation)
    boolean existsBySlugIgnoreCaseAndDeletedAtIsNullAndIdNot(String slug, Long id);

    // Find category by name (case-insensitive) excluding deleted ones
    Optional<Category> findByNameIgnoreCaseAndDeletedAtIsNull(String name);

    // Find categories with complex criteria
    @Query("SELECT c FROM Category c WHERE " +
           "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:slug IS NULL OR LOWER(c.slug) LIKE LOWER(CONCAT('%', :slug, '%'))) AND " +
           "c.deletedAt IS NULL")
    Page<Category> findByCriteria(@Param("name") String name, 
                                  @Param("slug") String slug,
                                  Pageable pageable);
}
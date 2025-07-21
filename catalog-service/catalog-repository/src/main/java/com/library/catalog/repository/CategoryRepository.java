package com.library.catalog.repository;

import com.library.catalog.repository.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // Find active categories with pagination
    Page<Category> findByDeleteFlagFalse(Pageable pageable);

    // Search categories by name (case-insensitive) excluding deleted ones
    Page<Category> findByNameContainingIgnoreCaseAndDeleteFlagFalse(String name, Pageable pageable);

    // Find active category by ID
    Optional<Category> findByIdAndDeleteFlagFalse(Integer id);

    // Check if active category exists by ID
    boolean existsByIdAndDeleteFlagFalse(Integer id);

    // Check if active category exists by name (case-insensitive)
    boolean existsByNameIgnoreCaseAndDeleteFlagFalse(String name);

    // Check if active category exists by slug (case-insensitive)
    boolean existsBySlugIgnoreCaseAndDeleteFlagFalse(String slug);

    // Find active category by name (case-insensitive)
    Optional<Category> findByNameIgnoreCaseAndDeleteFlagFalse(String name);

    // Find active category by slug (case-insensitive)
    Optional<Category> findBySlugIgnoreCaseAndDeleteFlagFalse(String slug);

    // Check if category with name exists excluding specific ID (for update validation)
    boolean existsByNameIgnoreCaseAndDeleteFlagFalseAndIdNot(String name, Integer id);

    // Check if category with slug exists excluding specific ID (for update validation)
    boolean existsBySlugIgnoreCaseAndDeleteFlagFalseAndIdNot(String slug, Integer id);
}
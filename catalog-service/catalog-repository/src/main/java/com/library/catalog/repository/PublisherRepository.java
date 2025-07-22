package com.library.catalog.repository;

import com.library.catalog.repository.entity.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Integer> {

    // Find active publishers with pagination
    Page<Publisher> findByDeleteFlagFalse(Pageable pageable);

    // Search publishers by name (case-insensitive) excluding deleted ones
    Page<Publisher> findByNameContainingIgnoreCaseAndDeleteFlagFalse(String name, Pageable pageable);

    // Find active publisher by ID
    Optional<Publisher> findByIdAndDeleteFlagFalse(Integer id);

    // Check if active publisher exists by ID
    boolean existsByIdAndDeleteFlagFalse(Integer id);

    // Check if active publisher exists by name (case-insensitive)
    boolean existsByNameIgnoreCaseAndDeleteFlagFalse(String name);

    // Find active publisher by name (case-insensitive)
    Optional<Publisher> findByNameIgnoreCaseAndDeleteFlagFalse(String name);

    // Check if publisher with name exists excluding specific ID (for update validation)
    boolean existsByNameIgnoreCaseAndDeleteFlagFalseAndIdNot(String name, Integer id);
}
package com.library.catalog.repository;

import com.library.catalog.repository.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {

    Page<Author> findByDeleteFlagFalse(Pageable pageable);

    Page<Author> findByNameContainingIgnoreCaseAndDeleteFlagFalse(String name, Pageable pageable);

    Optional<Author> findByIdAndDeleteFlagFalse(Integer id);

    boolean existsByIdAndDeleteFlagFalse(Integer id);

    boolean existsByNameIgnoreCaseAndDeleteFlagFalse(String name);

    Optional<Author> findByNameIgnoreCaseAndDeleteFlagFalse(String name);
}
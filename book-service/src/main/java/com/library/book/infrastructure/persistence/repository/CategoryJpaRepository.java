package com.library.book.infrastructure.persistence.repository;

import com.library.book.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
    Page<CategoryEntity> findAllByDeleteFlg(boolean deleteFlg, Pageable pageable);
    boolean existsByNameOrSlug(String name, String slug);
}
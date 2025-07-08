package com.library.book.infrastructure.persistence.repository;

import com.library.book.infrastructure.persistence.entity.CategoryJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {
    Page<CategoryJpaEntity> findAllByDeleteFlg(boolean deleteFlg, Pageable pageable);
    boolean existsByNameOrSlug(String name, String slug);
}
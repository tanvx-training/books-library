package com.library.book.infrastructure.persistence.repository;

import com.library.book.infrastructure.persistence.entity.PublisherJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherJpaRepository extends JpaRepository<PublisherJpaEntity, Long> {
    Page<PublisherJpaEntity> findAllByDeleteFlg(boolean deleteFlg, Pageable pageable);
    boolean existsByName(String name);
}
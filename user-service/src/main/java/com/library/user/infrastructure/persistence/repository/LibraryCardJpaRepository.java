package com.library.user.infrastructure.persistence.repository;

import com.library.user.infrastructure.persistence.entity.LibraryCardJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryCardJpaRepository extends JpaRepository<LibraryCardJpaEntity, Long> {
    List<LibraryCardJpaEntity> findByUserId(Long userId);
}
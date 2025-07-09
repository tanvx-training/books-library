package com.library.book.infrastructure.persistence.repository;

import com.library.book.infrastructure.persistence.entity.AuthorJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorJpaRepository extends JpaRepository<AuthorJpaEntity, Long> {

    Page<AuthorJpaEntity> findAllByDeleteFlg(boolean deleteFlg, Pageable pageable);
}

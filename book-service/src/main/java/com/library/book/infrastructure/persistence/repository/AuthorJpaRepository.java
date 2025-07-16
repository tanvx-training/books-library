package com.library.book.infrastructure.persistence.repository;

import com.library.book.infrastructure.persistence.entity.AuthorJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorJpaRepository extends JpaRepository<AuthorJpaEntity, Long> {

    Page<AuthorJpaEntity> findAllByDeleteFlg(boolean deleteFlg, Pageable pageable);
    List<AuthorJpaEntity> findAllByDeleteFlg(boolean deleteFlg);
    boolean existsByNameAndDeleteFlg(String name, boolean deleteFlg);
}

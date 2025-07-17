package com.library.book.infrastructure.persistence.repository;

import com.library.book.infrastructure.persistence.entity.AuthorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorJpaRepository extends JpaRepository<AuthorEntity, Long> {

    Page<AuthorEntity> findAllByDeleteFlg(boolean deleteFlg, Pageable pageable);
    List<AuthorEntity> findAllByDeleteFlg(boolean deleteFlg);
    boolean existsByNameAndDeleteFlg(String name, boolean deleteFlg);
}

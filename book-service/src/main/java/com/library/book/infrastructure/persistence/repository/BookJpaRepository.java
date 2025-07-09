package com.library.book.infrastructure.persistence.repository;

import com.library.book.infrastructure.persistence.entity.BookJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookJpaRepository extends JpaRepository<BookJpaEntity, Long> {

    Page<BookJpaEntity> findAllByDeleteFlg(boolean deleteFlg, Pageable pageable);

    Page<BookJpaEntity> findAllByTitleContainingIgnoreCaseAndDeleteFlg(String title, boolean deleteFlg, Pageable pageable);

    @Query("SELECT b FROM BookJpaEntity b JOIN b.categoryIds c WHERE c IN :categoryIds AND b.deleteFlg = :deleteFlg")
    Page<BookJpaEntity> findAllByCategoryIdsAndDeleteFlg(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("deleteFlg") boolean deleteFlg,
            Pageable pageable);

    boolean existsByIsbn(String isbn);
} 
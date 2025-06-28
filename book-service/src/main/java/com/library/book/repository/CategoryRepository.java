package com.library.book.repository;

import com.library.book.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findAllByDeleteFlg(boolean deleteFlg, Pageable pageable);

    boolean existsByNameOrSlug(String name, String slug);
}

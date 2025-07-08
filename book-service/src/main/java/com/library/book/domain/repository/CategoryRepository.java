package com.library.book.domain.repository;

import com.library.book.domain.model.category.Category;
import com.library.book.domain.model.category.CategoryId;
import com.library.book.domain.model.category.CategoryName;
import com.library.book.domain.model.category.CategorySlug;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(CategoryId id);
    Page<Category> findAll(int page, int size);
    long count();
    boolean existsByNameOrSlug(CategoryName name, CategorySlug slug);
    void delete(Category category);
}
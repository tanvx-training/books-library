package com.library.book.domain.service;

import com.library.book.domain.model.category.Category;
import com.library.book.domain.model.category.CategoryDescription;
import com.library.book.domain.model.category.CategoryName;
import com.library.book.domain.model.category.CategorySlug;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryDomainService {

    public Category createNewCategory(String name, String slug, String description, String createdByKeycloakId) {
        CategoryName categoryName = CategoryName.of(name);
        CategorySlug categorySlug = CategorySlug.of(slug);
        CategoryDescription categoryDescription = description != null ?
                CategoryDescription.of(description) : CategoryDescription.empty();

        return Category.create(categoryName, categorySlug, categoryDescription, createdByKeycloakId);
    }
}
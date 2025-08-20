package com.library.catalog.service;

import com.library.catalog.dto.request.CategorySearchRequest;
import com.library.catalog.dto.request.CreateCategoryRequest;
import com.library.catalog.dto.request.UpdateCategoryRequest;
import com.library.catalog.dto.response.CategoryResponse;
import com.library.catalog.dto.response.PagedCategoryResponse;

import java.util.UUID;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse getCategoryById(UUID publicId);

    PagedCategoryResponse getAllCategories(CategorySearchRequest request);

    CategoryResponse updateCategory(UUID publicId, UpdateCategoryRequest request);

    void deleteCategory(UUID publicId);
}
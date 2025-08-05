package com.library.catalog.business;

import com.library.catalog.business.dto.request.CategorySearchRequest;
import com.library.catalog.business.dto.request.CreateCategoryRequest;
import com.library.catalog.business.dto.request.UpdateCategoryRequest;
import com.library.catalog.business.dto.response.CategoryResponse;
import com.library.catalog.business.dto.response.PagedCategoryResponse;

import java.util.UUID;

public interface CategoryBusiness {

    CategoryResponse createCategory(CreateCategoryRequest request, String currentUser);

    CategoryResponse getCategoryById(UUID publicId);

    PagedCategoryResponse getAllCategories(CategorySearchRequest request);

    CategoryResponse updateCategory(UUID publicId, UpdateCategoryRequest request, String currentUser);

    void deleteCategory(UUID publicId, String currentUser);
}
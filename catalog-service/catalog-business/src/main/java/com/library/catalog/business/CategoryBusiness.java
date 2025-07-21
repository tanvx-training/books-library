package com.library.catalog.business;

import com.library.catalog.business.dto.request.CreateCategoryRequest;
import com.library.catalog.business.dto.request.UpdateCategoryRequest;
import com.library.catalog.business.dto.response.CategoryResponse;
import com.library.catalog.business.dto.response.PagedCategoryResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryBusiness {

    CategoryResponse createCategory(CreateCategoryRequest request, String currentUser);

    CategoryResponse getCategoryById(Integer id);

    PagedCategoryResponse getAllCategories(Pageable pageable);

    PagedCategoryResponse searchCategoriesByName(String name, Pageable pageable);

    CategoryResponse updateCategory(Integer id, UpdateCategoryRequest request, String currentUser);

    void deleteCategory(Integer id, String currentUser);
}
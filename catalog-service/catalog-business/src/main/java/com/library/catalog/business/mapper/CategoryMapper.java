package com.library.catalog.business.mapper;

import com.library.catalog.repository.entity.Category;
import com.library.catalog.business.dto.request.CreateCategoryRequest;
import com.library.catalog.business.dto.request.UpdateCategoryRequest;
import com.library.catalog.business.dto.response.CategoryResponse;
import com.library.catalog.business.dto.response.PagedCategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public Category toEntity(CreateCategoryRequest request) {
        if (request == null) {
            return null;
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        // deleteFlag is set to false by default in entity constructor
        // audit fields (createdAt, updatedAt, createdBy, updatedBy) are handled by JPA/service layer
        
        return category;
    }

    public void updateEntity(Category entity, UpdateCategoryRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setName(request.getName());
        entity.setSlug(request.getSlug());
        entity.setDescription(request.getDescription());
        // audit fields (updatedAt, updatedBy) are handled by JPA/service layer
    }

    public CategoryResponse toResponse(Category entity) {
        if (entity == null) {
            return null;
        }

        CategoryResponse response = new CategoryResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setSlug(entity.getSlug());
        response.setDescription(entity.getDescription());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());

        return response;
    }

    public List<CategoryResponse> toResponseList(List<Category> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PagedCategoryResponse toPagedResponse(Page<Category> page) {
        if (page == null) {
            return null;
        }

        PagedCategoryResponse response = new PagedCategoryResponse();
        
        // Convert content
        List<CategoryResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        response.setContent(content);
        
        // Set pagination metadata
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());

        return response;
    }
}
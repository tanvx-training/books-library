package com.library.catalog.business.impl;

import com.library.catalog.business.CategoryBusiness;
import com.library.catalog.business.aop.exception.EntityNotFoundException;
import com.library.catalog.business.dto.request.CategorySearchRequest;
import com.library.catalog.business.dto.request.CreateCategoryRequest;
import com.library.catalog.business.dto.request.UpdateCategoryRequest;
import com.library.catalog.business.dto.response.CategoryResponse;
import com.library.catalog.business.dto.response.PagedCategoryResponse;
import com.library.catalog.business.kafka.publisher.AuditService;
import com.library.catalog.business.mapper.CategoryMapper;
import com.library.catalog.business.util.EntityExceptionUtils;
import com.library.catalog.repository.CategoryRepository;
import com.library.catalog.repository.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryBusinessImpl implements CategoryBusiness {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AuditService auditService;

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request, String currentUser) {

        // Check for duplicate name
        EntityExceptionUtils.requireNoDuplicate(
                categoryRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(request.getName()),
                "Category", "name", request.getName()
        );
        // Check for duplicate slug
        EntityExceptionUtils.requireNoDuplicate(
                categoryRepository.existsBySlugIgnoreCaseAndDeletedAtIsNull(request.getSlug()),
                "Category", "slug", request.getSlug()
        );
        // Convert DTO to entity
        Category category = categoryMapper.toEntity(request);

        // Set audit fields
        category.setCreatedBy(currentUser);
        category.setUpdatedBy(currentUser);
        // Save to database
        categoryRepository.save(category);
        // Publish audit event for category creation
        auditService.publishCreateEvent("Category", category.getPublicId().toString(), category, currentUser);
        // Convert to response DTO
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID publicId) {

        Category category = categoryRepository.findByPublicIdAndDeletedAtIsNull(publicId)
                .orElseThrow(() -> EntityNotFoundException.forEntity("Category", publicId));
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedCategoryResponse getAllCategories(CategorySearchRequest request) {

        if (StringUtils.hasText(request.getName()) || StringUtils.hasText(request.getSlug())) {
            return categoryMapper.toPagedResponse(
                    categoryRepository.findByCriteria(request.getName(), request.getSlug(), request.toPageable())
            );
        }
        return categoryMapper.toPagedResponse(categoryRepository.findByDeletedAtIsNull(request.toPageable()));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID publicId, UpdateCategoryRequest request, String currentUser) {

        Category existingCategory = categoryRepository.findByPublicIdAndDeletedAtIsNull(publicId)
                .orElseThrow(() -> EntityNotFoundException.forEntity("Category", publicId));
        // Check for duplicate name (excluding current category)
        EntityExceptionUtils.requireNoDuplicate(
                categoryRepository.existsByNameIgnoreCaseAndDeletedAtIsNullAndIdNot(request.getName(), existingCategory.getId()),
                "Category", "name", request.getName()
        );
        // Check for duplicate slug (excluding current category)
        EntityExceptionUtils.requireNoDuplicate(
                categoryRepository.existsBySlugIgnoreCaseAndDeletedAtIsNullAndIdNot(request.getSlug(), existingCategory.getId()),
                "Category", "slug", request.getSlug()
        );

        // Store old values for audit event
        Category oldCategory = getOldCategory(existingCategory);
        // Update entity with new values
        categoryMapper.updateEntity(existingCategory, request);
        existingCategory.setUpdatedBy(currentUser);
        // Save updated category
        Category updatedCategory = categoryRepository.save(existingCategory);
        // Publish audit event with old and new values
        auditService.publishUpdateEvent("Category", updatedCategory.getPublicId().toString(), 
                oldCategory, updatedCategory, currentUser);
        // Convert to response DTO
        return categoryMapper.toResponse(updatedCategory);
    }

    private static Category getOldCategory(Category existingCategory) {
        Category oldCategory = new Category();
        oldCategory.setId(existingCategory.getId());
        oldCategory.setPublicId(existingCategory.getPublicId());
        oldCategory.setName(existingCategory.getName());
        oldCategory.setSlug(existingCategory.getSlug());
        oldCategory.setDescription(existingCategory.getDescription());
        oldCategory.setDeletedAt(existingCategory.getDeletedAt());
        oldCategory.setCreatedAt(existingCategory.getCreatedAt());
        oldCategory.setUpdatedAt(existingCategory.getUpdatedAt());
        oldCategory.setCreatedBy(existingCategory.getCreatedBy());
        oldCategory.setUpdatedBy(existingCategory.getUpdatedBy());
        return oldCategory;
    }

    @Override
    @Transactional
    public void deleteCategory(UUID publicId, String currentUser) {

        Category existingCategory = categoryRepository.findByPublicIdAndDeletedAtIsNull(publicId)
                .orElseThrow(() -> EntityNotFoundException.forEntity("Category", publicId));
        // Store old values for audit before deletion
        Category oldCategory = getOldCategory(existingCategory);
        // Perform soft deletion using timestamp
        existingCategory.markAsDeleted();
        existingCategory.setUpdatedBy(currentUser);
        // Save updated entity
        categoryRepository.save(existingCategory);
        // Publish audit event for category deletion
        auditService.publishDeleteEvent("Category", existingCategory.getPublicId().toString(), oldCategory, currentUser);
    }
}
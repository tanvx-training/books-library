package com.library.catalog.business.impl;

import com.library.catalog.business.CategoryBusiness;
import com.library.catalog.business.aop.exception.EntityNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryBusinessImpl implements CategoryBusiness {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AuditService auditService;

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request, String currentUser) {

        // Validate required fields
        EntityExceptionUtils.requireNonEmpty(request.getName(), "Category", "name");
        EntityExceptionUtils.requireNonEmpty(request.getSlug(), "Category", "slug");

        // Check for duplicate name
        EntityExceptionUtils.requireNoDuplicate(
                categoryRepository.existsByNameIgnoreCaseAndDeleteFlagFalse(request.getName()),
                "Category", "name", request.getName()
        );

        // Check for duplicate slug
        EntityExceptionUtils.requireNoDuplicate(
                categoryRepository.existsBySlugIgnoreCaseAndDeleteFlagFalse(request.getSlug()),
                "Category", "slug", request.getSlug()
        );

        // Convert DTO to entity
        Category category = categoryMapper.toEntity(request);
        // Set audit fields
        category.setCreatedBy(currentUser);
        category.setUpdatedBy(currentUser);
        // Save to database
        Category savedCategory = categoryRepository.save(category);

        // Publish audit event for category creation
        auditService.publishCreateEvent("Category", savedCategory.getId().toString(), savedCategory, currentUser);

        // Convert to response DTO
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Integer id) {

        EntityExceptionUtils.requireNonNull(id, "Category", "id");

        Category category = categoryRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> EntityNotFoundException.forEntity("Category", id));

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedCategoryResponse getAllCategories(Pageable pageable) {

        // Retrieve categories from database
        Page<Category> categoryPage = categoryRepository.findByDeleteFlagFalse(pageable);
        // Convert to response DTO
        return categoryMapper.toPagedResponse(categoryPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedCategoryResponse searchCategoriesByName(String name, Pageable pageable) {

        String searchName = EntityExceptionUtils.requireNonEmpty(name, "Category", "search name");

        // Search categories by name
        Page<Category> categoryPage = categoryRepository.findByNameContainingIgnoreCaseAndDeleteFlagFalse(
                searchName, pageable);
        return categoryMapper.toPagedResponse(categoryPage);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Integer id, UpdateCategoryRequest request, String currentUser) {
        
        // Validate required parameters
        EntityExceptionUtils.requireNonNull(id, "Category", "id");
        EntityExceptionUtils.requireNonNull(request, "Category", "update request");
        EntityExceptionUtils.requireNonEmpty(request.getName(), "Category", "name");
        EntityExceptionUtils.requireNonEmpty(request.getSlug(), "Category", "slug");

        // Find existing category
        Category existingCategory = categoryRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> EntityNotFoundException.forEntity("Category", id));

        // Store old values for audit event
        Category oldCategory = new Category();
        oldCategory.setId(existingCategory.getId());
        oldCategory.setName(existingCategory.getName());
        oldCategory.setSlug(existingCategory.getSlug());
        oldCategory.setDescription(existingCategory.getDescription());
        oldCategory.setCreatedAt(existingCategory.getCreatedAt());
        oldCategory.setUpdatedAt(existingCategory.getUpdatedAt());
        oldCategory.setCreatedBy(existingCategory.getCreatedBy());
        oldCategory.setUpdatedBy(existingCategory.getUpdatedBy());

        // Check for duplicate name (excluding current category)
        EntityExceptionUtils.requireNoDuplicate(
                categoryRepository.existsByNameIgnoreCaseAndDeleteFlagFalseAndIdNot(request.getName(), id),
                "Category", "name", request.getName()
        );

        // Check for duplicate slug (excluding current category)
        EntityExceptionUtils.requireNoDuplicate(
                categoryRepository.existsBySlugIgnoreCaseAndDeleteFlagFalseAndIdNot(request.getSlug(), id),
                "Category", "slug", request.getSlug()
        );

        // Update entity with new values
        categoryMapper.updateEntity(existingCategory, request);
        existingCategory.setUpdatedBy(currentUser);

        // Save updated category
        Category updatedCategory = categoryRepository.save(existingCategory);

        // Publish audit event with old and new values
        auditService.publishUpdateEvent("Category", updatedCategory.getId().toString(), 
                oldCategory, updatedCategory, currentUser);

        // Convert to response DTO
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Integer id, String currentUser) {

        EntityExceptionUtils.requireNonNull(id, "Category", "id");

        Category existingCategory = categoryRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> EntityNotFoundException.forEntity("Category", id));

        // Store old values for audit before deletion
        Category oldCategory = new Category();
        oldCategory.setId(existingCategory.getId());
        oldCategory.setName(existingCategory.getName());
        oldCategory.setSlug(existingCategory.getSlug());
        oldCategory.setDescription(existingCategory.getDescription());
        oldCategory.setDeleteFlag(existingCategory.getDeleteFlag());
        oldCategory.setCreatedAt(existingCategory.getCreatedAt());
        oldCategory.setUpdatedAt(existingCategory.getUpdatedAt());
        oldCategory.setCreatedBy(existingCategory.getCreatedBy());
        oldCategory.setUpdatedBy(existingCategory.getUpdatedBy());

        // Perform soft deletion
        existingCategory.setDeleteFlag(true);
        existingCategory.setUpdatedBy(currentUser);
        // Save updated entity
        categoryRepository.save(existingCategory);

        // Publish audit event for category deletion
        auditService.publishDeleteEvent("Category", existingCategory.getId().toString(), oldCategory, currentUser);
    }
}
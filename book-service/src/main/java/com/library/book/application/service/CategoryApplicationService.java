package com.library.book.application.service;

import com.library.book.application.dto.request.CategoryCreateRequest;
import com.library.book.application.dto.request.PaginatedRequest;
import com.library.book.application.dto.response.CategoryResponse;
import com.library.book.application.dto.response.PaginatedResponse;
import com.library.book.application.exception.CategoryApplicationException;
import com.library.book.application.exception.CategoryNotFoundException;
import com.library.book.domain.exception.CategoryDomainException;
import com.library.book.domain.exception.InvalidCategoryDataException;
import com.library.book.domain.model.category.Category;
import com.library.book.domain.model.category.CategoryId;
import com.library.book.domain.model.category.CategoryName;
import com.library.book.domain.model.category.CategorySlug;
import com.library.book.domain.repository.BookRepository;
import com.library.book.domain.repository.CategoryRepository;
import com.library.book.domain.service.CategoryDomainService;
import com.library.book.infrastructure.enums.LogLevel;
import com.library.book.infrastructure.enums.OperationType;
import com.library.book.infrastructure.logging.Loggable;
import com.library.book.infrastructure.persistence.mapper.BookEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryApplicationService {

    private final CategoryRepository categoryRepository;
    private final CategoryDomainService categoryDomainService;
    private final BookRepository bookRepository;
    private final BookEntityMapper bookMapper;

    @Transactional(readOnly = true)
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.READ,
            resourceType = "Category",
            logReturnValue = false,
            performanceThresholdMs = 800L,
            messagePrefix = "CATEGORY_APP_SERVICE_LIST"
    )
    public PaginatedResponse<CategoryResponse> getAllCategories(PaginatedRequest paginatedRequest) {
        Page<Category> categories = categoryRepository.findAll(
                paginatedRequest.getPage(),
                paginatedRequest.getSize()
        );

        Page<CategoryResponse> categoryResponses = categories.map(this::mapToCategoryResponse);

        return PaginatedResponse.from(categoryResponses);
    }

    @Transactional
    @Loggable(
            level = LogLevel.ADVANCED,
            operationType = OperationType.CREATE,
            resourceType = "Category",
            performanceThresholdMs = 1500L,
            messagePrefix = "CATEGORY_APP_SERVICE_CREATE"
    )
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        try {
            // Check if category with the same name or slug already exists
            if (categoryRepository.existsByNameOrSlug(
                    CategoryName.of(request.getName()),
                    CategorySlug.of(request.getSlug()))) {
                throw new CategoryApplicationException("Category with name '" + request.getName() +
                        "' or slug '" + request.getSlug() + "' already exists");
            }

            Category category = categoryDomainService.createNewCategory(
                    request.getName(),
                    request.getSlug(),
                    request.getDescription(),
                    "SYSTEM" // TODO: Add user context
            );

            Category savedCategory = categoryRepository.save(category);

            // Xử lý domain events nếu cần
            // eventPublisher.publish(savedCategory.getDomainEvents());

            return mapToCategoryResponse(savedCategory);
        } catch (InvalidCategoryDataException e) {
            log.error("Invalid category data: {}", e.getMessage());
            throw e; // Rethrow để được xử lý bởi exception handler
        } catch (CategoryDomainException e) {
            log.error("Domain exception when creating category: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error when creating category", e);
            throw new CategoryApplicationException("Failed to create category", e);
        }
    }

    @Transactional(readOnly = true)
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.READ,
            resourceType = "Category",
            performanceThresholdMs = 500L,
            messagePrefix = "CATEGORY_APP_SERVICE_GET_BY_ID"
    )
    public CategoryResponse getCategoryById(Long id) {
        return categoryRepository.findById(new CategoryId(id))
                .map(this::mapToCategoryResponse)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId().getValue())
                .name(category.getName().getValue())
                .slug(category.getSlug().getValue())
                .description(category.getDescription().getValue())
                .build();
    }

    private List<CategoryId> convertDomainCategoryToEntity(Category domainCategory) {
        return List.of(domainCategory.getId());
    }

}
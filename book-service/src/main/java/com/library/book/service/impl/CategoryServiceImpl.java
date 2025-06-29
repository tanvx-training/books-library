package com.library.book.service.impl;

import com.library.book.model.Category;
import com.library.book.service.CategoryService;
import com.library.book.repository.BookRepository;
import com.library.book.repository.CategoryRepository;
import com.library.book.dto.request.CategoryCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.book.dto.response.CategoryResponseDTO;
import com.library.book.utils.mapper.BookMapper;
import com.library.book.utils.mapper.CategoryMapper;
import com.library.common.aop.annotation.Loggable;
import com.library.common.dto.PaginatedRequest;
import com.library.common.aop.exception.ResourceExistedException;
import com.library.common.aop.exception.ResourceNotFoundException;
import com.library.common.dto.PaginatedResponse;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Category",
        logArguments = true,
        logReturnValue = false, // Don't log collections in service layer
        logExecutionTime = true,
        performanceThresholdMs = 800L,
        messagePrefix = "CATEGORY_SERVICE_LIST",
        customTags = {"layer=service", "transaction=readonly", "soft_delete_filter=true", "pagination=true"}
    )
    public PaginatedResponse<CategoryResponseDTO> getAllCategories(PaginatedRequest paginatedRequest) {
        Pageable pageable = paginatedRequest.toPageable();
        Page<CategoryResponseDTO> page = categoryRepository.findAllByDeleteFlg(Boolean.FALSE, pageable)
                .map(categoryMapper::toDto);
        return PaginatedResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Category",
        logArguments = true,
        logReturnValue = false, // Don't log book collections
        logExecutionTime = true,
        performanceThresholdMs = 1200L,
        messagePrefix = "CATEGORY_SERVICE_BOOKS",
        customTags = {
            "layer=service", 
            "transaction=readonly", 
            "relationship_query=true",
            "multi_entity_lookup=true",
            "pagination=true"
        }
    )
    public PaginatedResponse<BookResponseDTO> getBooksByCategory(Long categoryId, PaginatedRequest paginatedRequest) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        Pageable pageable = paginatedRequest.toPageable();
        Page<BookResponseDTO> page = bookRepository.findAllByCategoriesAndDeleteFlg(List.of(category), Boolean.FALSE, pageable)
                .map(bookMapper::toDto);
        return PaginatedResponse.from(page);
    }

    @Override
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "Category",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 1500L,
        messagePrefix = "CATEGORY_SERVICE_CREATE",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "business_validation=true",
            "uniqueness_check=true",
            "catalog_management=true"
        }
    )
    public CategoryResponseDTO createCategory(CategoryCreateDTO categoryCreateDTO) {
        if (categoryRepository.existsByNameOrSlug(categoryCreateDTO.getName(), categoryCreateDTO.getSlug())) {
            throw new ResourceExistedException("Category", "name/slug", categoryCreateDTO.getName() + "/" + categoryCreateDTO.getSlug());
        }
        Category category = categoryMapper.toEntity(categoryCreateDTO);
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }
}

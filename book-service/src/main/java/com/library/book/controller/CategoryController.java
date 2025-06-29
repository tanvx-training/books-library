package com.library.book.controller;

import com.library.book.service.CategoryService;
import com.library.book.dto.request.CategoryCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.book.dto.response.CategoryResponseDTO;
import com.library.common.aop.annotation.Loggable;
import com.library.common.dto.ApiResponse;
import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "Category",
        logArguments = true,
        logReturnValue = false, // Don't log large collections
        performanceThresholdMs = 1000L,
        messagePrefix = "CATEGORY_LIST",
        customTags = {"endpoint=getAllCategories", "pagination=true", "catalog_operation=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<CategoryResponseDTO>>> getAllCategories(
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategories(paginatedRequest)));
    }

    @GetMapping("/{categoryId}/books")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Category",
        logArguments = true,
        logReturnValue = false, // Books list can be large
        performanceThresholdMs = 1500L,
        messagePrefix = "CATEGORY_BOOKS",
        customTags = {"endpoint=getBooksByCategory", "relationship_query=true", "category_books_lookup=true", "pagination=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<BookResponseDTO>>> getBooksByCategory(
            @PathVariable Long categoryId,
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getBooksByCategory(categoryId, paginatedRequest)));
    }

    @PostMapping
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.CREATE,
        resourceType = "Category",
        logArguments = true,
        logReturnValue = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "CATEGORY_CREATION",
        customTags = {"endpoint=createCategory", "catalog_operation=true", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(@Valid @RequestBody CategoryCreateDTO categoryCreateDTO) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.createCategory(categoryCreateDTO)));

    }
}

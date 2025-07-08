package com.library.book.interfaces.rest;

import com.library.book.application.dto.request.CategoryCreateRequest;
import com.library.book.application.dto.response.BookResponse;
import com.library.book.application.dto.response.CategoryResponse;
import com.library.book.application.service.CategoryApplicationService;
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
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryApplicationService categoryApplicationService;

    @GetMapping
    @Loggable(
            level = LogLevel.BASIC,
            operationType = OperationType.READ,
            resourceType = "Category",
            logArguments = true,
            logReturnValue = false,
            performanceThresholdMs = 1000L,
            messagePrefix = "CATEGORY_LIST",
            customTags = {"endpoint=getAllCategories", "pagination=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<CategoryResponse>>> getAllCategories(
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                categoryApplicationService.getAllCategories(paginatedRequest)
        ));
    }

    @GetMapping("/{categoryId}")
    @Loggable(
            level = LogLevel.BASIC,
            operationType = OperationType.READ,
            resourceType = "Category",
            logArguments = true,
            logReturnValue = true,
            performanceThresholdMs = 500L,
            messagePrefix = "CATEGORY_DETAIL",
            customTags = {"endpoint=getCategoryById"}
    )
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable("categoryId") Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success(
                categoryApplicationService.getCategoryById(categoryId)
        ));
    }

//    @GetMapping("/{categoryId}/books")
//    @Loggable(
//            level = LogLevel.DETAILED,
//            operationType = OperationType.READ,
//            resourceType = "Category",
//            logArguments = true,
//            logReturnValue = false,
//            performanceThresholdMs = 1500L,
//            messagePrefix = "CATEGORY_BOOKS",
//            customTags = {"endpoint=getBooksByCategory", "relationship_query=true"}
//    )
//    public ResponseEntity<ApiResponse<PaginatedResponse<BookResponse>>> getBooksByCategory(
//            @PathVariable("categoryId") Long categoryId,
//            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
//        return ResponseEntity.ok(ApiResponse.success(
//                categoryApplicationService.getBooksByCategory(categoryId, paginatedRequest)
//        ));
//    }

    @PostMapping
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.CREATE,
            resourceType = "Category",
            logArguments = true,
            logReturnValue = true,
            performanceThresholdMs = 2000L,
            messagePrefix = "CATEGORY_CREATION",
            customTags = {"endpoint=createCategory", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @RequestBody @Valid CategoryCreateRequest categoryCreateRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                categoryApplicationService.createCategory(categoryCreateRequest)
        ));
    }
}
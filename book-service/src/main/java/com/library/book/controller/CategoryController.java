package com.library.book.controller;

import com.library.book.service.CategoryService;
import com.library.book.dto.request.CategoryCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.book.dto.response.CategoryResponseDTO;
import com.library.common.dto.ApiResponse;
import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;
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
    public ResponseEntity<ApiResponse<PaginatedResponse<CategoryResponseDTO>>> getAllCategories(
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategories(paginatedRequest)));
    }

    @GetMapping("/{categoryId}/books")
    public ResponseEntity<ApiResponse<PaginatedResponse<BookResponseDTO>>> getBooksByCategory(
            @PathVariable Long categoryId,
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getBooksByCategory(categoryId, paginatedRequest)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(@Valid @RequestBody CategoryCreateDTO categoryCreateDTO) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.createCategory(categoryCreateDTO)));

    }
}

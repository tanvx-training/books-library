package com.library.catalog.controller.rest;

import com.library.catalog.controller.util.UserContextUtil;
import com.library.catalog.business.CategoryBusiness;
import com.library.catalog.business.dto.request.CreateCategoryRequest;
import com.library.catalog.business.dto.request.UpdateCategoryRequest;
import com.library.catalog.business.dto.response.CategoryResponse;
import com.library.catalog.business.dto.response.PagedCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/v1/categories")
@Validated
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryBusiness categoryBusiness;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        String currentUser = UserContextUtil.getCurrentUser();
        CategoryResponse response = categoryBusiness.createCategory(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable @Positive(message = "Category ID must be positive") Integer id) {
        CategoryResponse response = categoryBusiness.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedCategoryResponse> getAllCategories(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be non-negative") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size must not exceed 100") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortBy);
        PagedCategoryResponse response = categoryBusiness.getAllCategories(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedCategoryResponse> searchCategories(
            @RequestParam @NotBlank(message = "Search name cannot be blank") String name,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be non-negative") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size must not exceed 100") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortBy);
        PagedCategoryResponse response = categoryBusiness.searchCategoriesByName(name, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable @Positive(message = "Category ID must be positive") Integer id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        
        String currentUser = UserContextUtil.getCurrentUser();
        CategoryResponse response = categoryBusiness.updateCategory(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @Positive(message = "Category ID must be positive") Integer id) {
        String currentUser = UserContextUtil.getCurrentUser();
        categoryBusiness.deleteCategory(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
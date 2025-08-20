package com.library.catalog.controller;

import com.library.catalog.dto.request.CategorySearchRequest;
import com.library.catalog.dto.request.CreateCategoryRequest;
import com.library.catalog.dto.request.UpdateCategoryRequest;
import com.library.catalog.dto.response.CategoryResponse;
import com.library.catalog.dto.response.PagedCategoryResponse;
import com.library.catalog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@Validated
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {

        CategoryResponse response = categoryService.createCategory(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{public_id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable("public_id") String publicId) {

        UUID uuid = UUID.fromString(publicId);
        CategoryResponse response = categoryService.getCategoryById(uuid);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedCategoryResponse> getAllCategories(@Valid @ModelAttribute CategorySearchRequest request) {

        PagedCategoryResponse response = categoryService.getAllCategories(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{public_id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable("public_id") String publicId,
            @Valid @RequestBody UpdateCategoryRequest request) {

        UUID uuid = UUID.fromString(publicId);
        CategoryResponse response = categoryService.updateCategory(uuid, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{public_id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("public_id") String publicId) {

        UUID uuid = UUID.fromString(publicId);
        categoryService.deleteCategory(uuid);
        return ResponseEntity.noContent().build();
    }
}
package com.library.catalog.controller.rest;

import com.library.catalog.business.dto.request.CategorySearchRequest;
import com.library.catalog.business.CategoryBusiness;
import com.library.catalog.business.dto.request.CreateCategoryRequest;
import com.library.catalog.business.dto.request.UpdateCategoryRequest;
import com.library.catalog.business.dto.response.CategoryResponse;
import com.library.catalog.business.dto.response.PagedCategoryResponse;
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

    private final CategoryBusiness categoryBusiness;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {

        CategoryResponse response = categoryBusiness.createCategory(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{public_id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable("public_id") String publicId) {

        UUID uuid = UUID.fromString(publicId);
        CategoryResponse response = categoryBusiness.getCategoryById(uuid);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedCategoryResponse> getAllCategories(@Valid @ModelAttribute CategorySearchRequest request) {

        PagedCategoryResponse response = categoryBusiness.getAllCategories(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{public_id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable("public_id") String publicId,
            @Valid @RequestBody UpdateCategoryRequest request) {

        UUID uuid = UUID.fromString(publicId);
        CategoryResponse response = categoryBusiness.updateCategory(uuid, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{public_id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("public_id") String publicId) {

        UUID uuid = UUID.fromString(publicId);
        categoryBusiness.deleteCategory(uuid);
        return ResponseEntity.noContent().build();
    }
}
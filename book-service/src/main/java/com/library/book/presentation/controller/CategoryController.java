package com.library.book.presentation.controller;

import com.library.book.domain.service.CategoryService;
import com.library.book.presentation.dto.response.CategoryResponseDTO;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<PageResponseDTO<CategoryResponseDTO>> getAllCategories(
            @Valid @ModelAttribute PageRequestDTO pageRequestDTO) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageRequestDTO));
    }
}

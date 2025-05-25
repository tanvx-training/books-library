package com.library.book.presentation.controller;

import com.library.book.domain.service.CategoryService;
import com.library.book.presentation.dto.request.CategoryCreateDTO;
import com.library.book.presentation.dto.response.BookResponseDTO;
import com.library.book.presentation.dto.response.CategoryResponseDTO;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{categoryId}/books")
    public ResponseEntity<PageResponseDTO<BookResponseDTO>> getBooksByCategory(
            @PathVariable Long categoryId,
            @Valid @ModelAttribute PageRequestDTO pageRequestDTO) {
        return ResponseEntity.ok(categoryService.getBooksByCategory(categoryId, pageRequestDTO));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryCreateDTO categoryCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(categoryCreateDTO));
    }
}

package com.library.book.controller;

import com.library.book.service.AuthorService;
import com.library.book.dto.request.AuthorCreateDTO;
import com.library.book.dto.response.AuthorResponseDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.common.dto.ApiResponse;
import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<AuthorResponseDTO>>> getAllCategories(
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(authorService.getAllAuthors(paginatedRequest)));
    }

    @GetMapping("/{authorId}/books")
    public ResponseEntity<ApiResponse<List<BookResponseDTO>>> getBooksByAuthor(
            @PathVariable("authorId") Long authorId) {
        return ResponseEntity.ok(ApiResponse.success(authorService.getBooksByAuthor(authorId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AuthorResponseDTO>> createAuthor(
            @RequestBody @Valid AuthorCreateDTO authorCreateDTO) {
        return ResponseEntity.ok(ApiResponse.success(authorService.createAuthor(authorCreateDTO)));
    }
}

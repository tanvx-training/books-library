package com.library.book.controller;

import com.library.book.service.AuthorService;
import com.library.book.dto.request.AuthorCreateDTO;
import com.library.book.dto.response.AuthorResponseDTO;
import com.library.book.dto.response.BookResponseDTO;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "Author",
        logArguments = true,
        logReturnValue = false, // Don't log large collections
        performanceThresholdMs = 1000L,
        messagePrefix = "AUTHOR_LIST",
        customTags = {"endpoint=getAllAuthors", "pagination=true", "catalog_operation=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<AuthorResponseDTO>>> getAllCategories(
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(authorService.getAllAuthors(paginatedRequest)));
    }

    @GetMapping("/{authorId}/books")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Author",
        logArguments = true,
        logReturnValue = false, // Books list can be large
        performanceThresholdMs = 1500L,
        messagePrefix = "AUTHOR_BOOKS",
        customTags = {"endpoint=getBooksByAuthor", "relationship_query=true", "author_books_lookup=true"}
    )
    public ResponseEntity<ApiResponse<List<BookResponseDTO>>> getBooksByAuthor(
            @PathVariable("authorId") Long authorId) {
        return ResponseEntity.ok(ApiResponse.success(authorService.getBooksByAuthor(authorId)));
    }

    @PostMapping
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.CREATE,
        resourceType = "Author",
        logArguments = true,
        logReturnValue = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "AUTHOR_CREATION",
        customTags = {"endpoint=createAuthor", "catalog_operation=true", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<AuthorResponseDTO>> createAuthor(
            @RequestBody @Valid AuthorCreateDTO authorCreateDTO) {
        return ResponseEntity.ok(ApiResponse.success(authorService.createAuthor(authorCreateDTO)));
    }
}

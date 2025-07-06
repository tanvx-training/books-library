package com.library.book.interfaces.rest;

import com.library.book.application.dto.request.AuthorCreateRequest;
import com.library.book.application.dto.response.AuthorResponse;
import com.library.book.application.service.AuthorApplicationService;
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
@RequestMapping("/api/v1/authors")
public class AuthorController {

    private final AuthorApplicationService authorApplicationService;

    @GetMapping
    @Loggable(
            level = LogLevel.BASIC,
            operationType = OperationType.READ,
            resourceType = "Author",
            logReturnValue = false,
            messagePrefix = "AUTHOR_LIST",
            customTags = {"endpoint=getAllAuthors", "pagination=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<AuthorResponse>>> getAllAuthors(
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                authorApplicationService.getAllAuthors(paginatedRequest)
        ));
    }

    @GetMapping("/{authorId}")
    @Loggable(
            level = LogLevel.BASIC,
            operationType = OperationType.READ,
            resourceType = "Author",
            performanceThresholdMs = 500L,
            messagePrefix = "AUTHOR_DETAIL",
            customTags = {"endpoint=getAuthorById"}
    )
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthorById(
            @PathVariable("authorId") Long authorId) {
        return ResponseEntity.ok(ApiResponse.success(
                authorApplicationService.getAuthorById(authorId)
        ));
    }

    @PostMapping
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.CREATE,
            resourceType = "Author",
            performanceThresholdMs = 2000L,
            messagePrefix = "AUTHOR_CREATION",
            customTags = {"endpoint=createAuthor", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<AuthorResponse>> createAuthor(
            @RequestBody @Valid AuthorCreateRequest authorCreateRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                authorApplicationService.createAuthor(authorCreateRequest)
        ));
    }
}
package com.library.book.controller;

import com.library.book.service.PublisherService;
import com.library.book.dto.request.PublisherCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.book.dto.response.PublisherResponseDTO;
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
@RequestMapping("/api/publishers")
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "Publisher",
        logArguments = true,
        logReturnValue = false, // Don't log large collections
        performanceThresholdMs = 1000L,
        messagePrefix = "PUBLISHER_LIST",
        customTags = {"endpoint=getAllPublishers", "pagination=true", "catalog_operation=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<PublisherResponseDTO>>> getAllPublishers(
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(publisherService.getAllPublishers(paginatedRequest)));
    }

    @GetMapping("/{publisherId}/books")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Publisher",
        logArguments = true,
        logReturnValue = false, // Books list can be large
        performanceThresholdMs = 1500L,
        messagePrefix = "PUBLISHER_BOOKS",
        customTags = {"endpoint=getBooksByPublisher", "relationship_query=true", "publisher_books_lookup=true", "pagination=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<BookResponseDTO>>> getBooksByPublisher(
            @PathVariable("publisherId") Long publisherId,
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(publisherService.getBooksByPublisher(publisherId, paginatedRequest)));
    }

    @PostMapping
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.CREATE,
        resourceType = "Publisher",
        logArguments = true,
        logReturnValue = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "PUBLISHER_CREATION",
        customTags = {"endpoint=createPublisher", "catalog_operation=true", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<PublisherResponseDTO>> createPublisher(
            @RequestBody @Valid PublisherCreateDTO publisherCreateDTO) {
        return ResponseEntity.ok(ApiResponse.success(publisherService.createPublisher(publisherCreateDTO)));
    }
}

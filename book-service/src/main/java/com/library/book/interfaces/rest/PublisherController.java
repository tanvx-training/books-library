package com.library.book.interfaces.rest;

import com.library.book.application.dto.request.PublisherCreateRequest;
import com.library.book.application.dto.response.PublisherResponse;
import com.library.book.application.service.PublisherApplicationService;
import com.library.book.infrastructure.enums.LogLevel;
import com.library.book.infrastructure.enums.OperationType;
import com.library.book.infrastructure.logging.Loggable;
import com.library.book.application.dto.request.PaginatedRequest;
import com.library.book.application.dto.response.PaginatedResponse;
import com.library.book.application.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/publishers")
public class PublisherController {

    private final PublisherApplicationService publisherApplicationService;

    @GetMapping
    @Loggable(
            level = LogLevel.BASIC,
            operationType = OperationType.READ,
            resourceType = "Publisher",
            logReturnValue = false,
            messagePrefix = "PUBLISHER_LIST",
            customTags = {"endpoint=getAllPublishers", "pagination=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<PublisherResponse>>> getAllPublishers(
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                publisherApplicationService.getAllPublishers(paginatedRequest)
        ));
    }

    @GetMapping("/{publisherId}")
    @Loggable(
            level = LogLevel.BASIC,
            operationType = OperationType.READ,
            resourceType = "Publisher",
            messagePrefix = "PUBLISHER_DETAIL",
            customTags = {"endpoint=getPublisherById"}
    )
    public ResponseEntity<ApiResponse<PublisherResponse>> getPublisherById(
            @PathVariable("publisherId") Long publisherId) {
        return ResponseEntity.ok(ApiResponse.success(
                publisherApplicationService.getPublisherById(publisherId)
        ));
    }

    @PostMapping
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.CREATE,
            resourceType = "Publisher",
            performanceThresholdMs = 2000L,
            messagePrefix = "PUBLISHER_CREATION",
            customTags = {"endpoint=createPublisher", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<PublisherResponse>> createPublisher(
            @RequestBody @Valid PublisherCreateRequest publisherCreateRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                publisherApplicationService.createPublisher(publisherCreateRequest)
        ));
    }
}
package com.library.book.interfaces.rest;

import com.library.book.application.dto.request.BookCopyCreateRequest;
import com.library.book.application.dto.request.BookCopyUpdateRequest;
import com.library.book.application.dto.response.BookCopyResponse;
import com.library.book.application.service.BookCopyApplicationService;
import com.library.common.aop.annotation.Loggable;
import com.library.common.dto.ApiResponse;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/copies")
public class BookCopyController {

    private final BookCopyApplicationService bookCopyApplicationService;

    @GetMapping("/books/{bookId}")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = false,
        performanceThresholdMs = 1000L,
        messagePrefix = "BOOK_COPIES_BY_BOOK",
        customTags = {"endpoint=getBookCopiesByBookId", "relationship_query=true", "inventory_check=true"}
    )
    public ResponseEntity<ApiResponse<List<BookCopyResponse>>> getBookCopiesByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(ApiResponse.success(bookCopyApplicationService.getBookCopiesByBookId(bookId)));
    }

    @PostMapping
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.CREATE,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "BOOK_COPY_CREATION",
        customTags = {"endpoint=addBookCopy", "inventory_management=true", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<BookCopyResponse>> addBookCopy(
            @Valid @RequestBody BookCopyCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(bookCopyApplicationService.addBookCopy(request)));
    }

    @GetMapping("/{bookCopyId}")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = true,
        performanceThresholdMs = 500L,
        messagePrefix = "BOOK_COPY_DETAIL",
        customTags = {"endpoint=getBookCopyById", "single_resource=true", "inventory_check=true"}
    )
    public ResponseEntity<ApiResponse<BookCopyResponse>> getBookCopyById(@PathVariable Long bookCopyId) {
        return ResponseEntity.ok(ApiResponse.success(bookCopyApplicationService.getBookCopyById(bookCopyId)));
    }

    @PutMapping("/{bookCopyId}")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.UPDATE,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "BOOK_COPY_UPDATE",
        customTags = {"endpoint=updateBookCopy", "inventory_management=true", "full_update=true"}
    )
    public ResponseEntity<ApiResponse<BookCopyResponse>> updateBookCopy(
            @PathVariable Long bookCopyId,
            @Valid @RequestBody BookCopyUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(bookCopyApplicationService.updateBookCopy(bookCopyId, request)));
    }

    @PatchMapping("/{bookCopyId}/status")
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 1500L,
        messagePrefix = "BOOK_COPY_STATUS_UPDATE",
        customTags = {
            "endpoint=updateBookCopyStatus", 
            "inventory_management=true", 
            "status_change=true",
            "business_critical=true",
            "availability_tracking=true"
        }
    )
    public ResponseEntity<ApiResponse<BookCopyResponse>> updateBookCopyStatus(
            @PathVariable Long bookCopyId,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success(bookCopyApplicationService.updateBookCopyStatus(bookCopyId, status)));
    }

    @DeleteMapping("/{bookCopyId}")
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.DELETE,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "BOOK_COPY_DELETE",
        customTags = {
            "endpoint=deleteBookCopy", 
            "inventory_management=true", 
            "data_removal=true",
            "business_critical=true",
            "audit_required=true"
        }
    )
    public ResponseEntity<ApiResponse<Boolean>> deleteBookCopy(@PathVariable Long bookCopyId) {
        boolean deleted = bookCopyApplicationService.deleteBookCopy(bookCopyId);
        return ResponseEntity.ok(ApiResponse.success(deleted));
    }
} 
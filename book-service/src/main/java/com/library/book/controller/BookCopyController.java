package com.library.book.controller;

import com.library.book.service.BookCopyService;
import com.library.book.dto.request.BookCopyRequestDTO;
import com.library.book.dto.request.BookCopyUpdateDTO;
import com.library.book.dto.response.BookCopyResponseDTO;
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

    private final BookCopyService bookCopyService;

    @GetMapping("/{bookId}/books")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = false, // Book copies list can be large
        performanceThresholdMs = 1000L,
        messagePrefix = "BOOK_COPIES_BY_BOOK",
        customTags = {"endpoint=getBookCopiesByBookId", "relationship_query=true", "inventory_check=true"}
    )
    public ResponseEntity<ApiResponse<List<BookCopyResponseDTO>>> getBookCopiesByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(ApiResponse.success(bookCopyService.getBookCopiesByBookId(bookId)));
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
    public ResponseEntity<ApiResponse<BookCopyResponseDTO>> addBookCopy(
            @Valid @RequestBody BookCopyRequestDTO bookCopyRequestDTO) {
        return ResponseEntity.ok(ApiResponse.success(bookCopyService.addBookCopy(bookCopyRequestDTO)));
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
    public ResponseEntity<ApiResponse<BookCopyResponseDTO>> getBookCopyById(@PathVariable Long bookCopyId) {
        return ResponseEntity.ok(ApiResponse.success(bookCopyService.getBookCopyById(bookCopyId)));
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
    public ResponseEntity<ApiResponse<BookCopyResponseDTO>> updateBookCopy(
            @PathVariable Long bookCopyId,
            @Valid @RequestBody BookCopyUpdateDTO bookCopyRequestDTO) {
        return ResponseEntity.ok(ApiResponse.success(bookCopyService.updateBookCopy(bookCopyId, bookCopyRequestDTO)));
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
    public ResponseEntity<ApiResponse<BookCopyResponseDTO>> updateBookCopyStatus(
            @PathVariable Long bookCopyId,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success(bookCopyService.updateBookCopyStatus(bookCopyId, status)));
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
        boolean deleted = bookCopyService.deleteBookCopy(bookCopyId);
        return ResponseEntity.ok(ApiResponse.success(deleted));
    }
} 
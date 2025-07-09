package com.library.book.interfaces.rest;

import com.library.book.application.dto.request.BookCreateRequest;
import com.library.book.application.dto.response.BookResponse;
import com.library.book.application.service.BookApplicationService;
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
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookApplicationService bookApplicationService;

    @GetMapping
    @Loggable(
            level = LogLevel.BASIC,
            operationType = OperationType.READ,
            resourceType = "Book",
            logReturnValue = false,
            messagePrefix = "BOOK_LIST",
            customTags = {"endpoint=getAllBooks", "pagination=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<BookResponse>>> getAllBooks(
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                bookApplicationService.getAllBooks(paginatedRequest)
        ));
    }

    @GetMapping("/{bookId}")
    @Loggable(
            level = LogLevel.BASIC,
            operationType = OperationType.READ,
            resourceType = "Book",
            performanceThresholdMs = 500L,
            messagePrefix = "BOOK_DETAIL",
            customTags = {"endpoint=getBookById"}
    )
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(
            @PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(ApiResponse.success(
                bookApplicationService.getBookById(bookId)
        ));
    }

    @GetMapping("/search")
    @Loggable(
            level = LogLevel.BASIC,
            operationType = OperationType.READ,
            resourceType = "Book",
            performanceThresholdMs = 800L,
            messagePrefix = "BOOK_SEARCH",
            customTags = {"endpoint=searchBooks", "pagination=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<BookResponse>>> searchBooks(
            @RequestParam("keyword") String keyword,
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                bookApplicationService.searchBooks(keyword, paginatedRequest)
        ));
    }

    @PostMapping
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.CREATE,
            resourceType = "Book",
            performanceThresholdMs = 2000L,
            messagePrefix = "BOOK_CREATION",
            customTags = {"endpoint=createBook", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<BookResponse>> createBook(
            @RequestBody @Valid BookCreateRequest bookCreateRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                bookApplicationService.createBook(bookCreateRequest)
        ));
    }

    @PutMapping("/{bookId}")
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.UPDATE,
            resourceType = "Book",
            performanceThresholdMs = 2000L,
            messagePrefix = "BOOK_UPDATE",
            customTags = {"endpoint=updateBook", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable("bookId") Long bookId,
            @RequestBody @Valid BookCreateRequest bookUpdateRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                bookApplicationService.updateBook(bookId, bookUpdateRequest)
        ));
    }

    @DeleteMapping("/{bookId}")
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.DELETE,
            resourceType = "Book",
            performanceThresholdMs = 1000L,
            messagePrefix = "BOOK_DELETION",
            customTags = {"endpoint=deleteBook", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<Void>> deleteBook(
            @PathVariable("bookId") Long bookId) {
        bookApplicationService.deleteBook(bookId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
} 
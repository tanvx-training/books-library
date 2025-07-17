package com.library.book.interfaces.rest;

import com.library.book.application.dto.request.BookCreateRequest;
import com.library.book.application.dto.response.BookResponse;
import com.library.book.application.service.BookApplicationService;
import com.library.book.infrastructure.enums.LogLevel;
import com.library.book.infrastructure.enums.OperationType;
import com.library.book.infrastructure.logging.Loggable;
import com.library.book.infrastructure.config.security.JwtAuthenticationService;
import com.library.book.infrastructure.config.security.RequireBookManagement;
import com.library.book.application.dto.request.PaginatedRequest;
import com.library.book.application.dto.response.PaginatedResponse;
import com.library.book.application.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookApplicationService bookApplicationService;
    private final JwtAuthenticationService jwtAuthenticationService;

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
    @RequireBookManagement
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
        
        JwtAuthenticationService.AuthenticatedUser currentUser = jwtAuthenticationService.getCurrentUser();
        
        return ResponseEntity.ok(ApiResponse.success(
                bookApplicationService.createBook(bookCreateRequest, currentUser)
        ));
    }

    @PutMapping("/{bookId}")
    @RequireBookManagement
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
        
        JwtAuthenticationService.AuthenticatedUser currentUser = jwtAuthenticationService.getCurrentUser();
        
        return ResponseEntity.ok(ApiResponse.success(
                bookApplicationService.updateBook(bookId, bookUpdateRequest, currentUser)
        ));
    }

    @DeleteMapping("/{bookId}")
    @RequireBookManagement
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.DELETE,
            resourceType = "Book",
            performanceThresholdMs = 1000L,
            messagePrefix = "BOOK_DELETION",
            customTags = {"endpoint=deleteBook", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable("bookId") Long bookId) {
        
        JwtAuthenticationService.AuthenticatedUser currentUser = jwtAuthenticationService.getCurrentUser();
        
        bookApplicationService.deleteBook(bookId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
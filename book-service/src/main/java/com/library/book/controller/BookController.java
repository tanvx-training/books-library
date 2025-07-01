package com.library.book.controller;

import com.library.book.service.BookService;
import com.library.book.dto.request.BookCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.book.utils.SecurityUtils;
import com.library.common.aop.annotation.Loggable;
import com.library.common.dto.ApiResponse;
import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "Book",
        logArguments = true,
        logReturnValue = false, // Don't log large result sets
        performanceThresholdMs = 1500L,
        messagePrefix = "BOOK_LIST",
        customTags = {"endpoint=getAllBooks", "catalog_operation=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<BookResponseDTO>>> getAllBooks(
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(bookService.getAllBooks(paginatedRequest)));
    }

    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.CREATE,
        resourceType = "Book",
        logArguments = true,
        logReturnValue = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "BOOK_CREATION",
        customTags = {"endpoint=createBook", "catalog_operation=true", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<BookResponseDTO>> createBook(@Valid @RequestBody BookCreateDTO bookCreateDTO) {
        return ResponseEntity.ok(ApiResponse.success(bookService.createBook(bookCreateDTO)));
    }

    @GetMapping("/{bookId}")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Book",
        logArguments = true,
        logReturnValue = true,
        performanceThresholdMs = 500L,
        messagePrefix = "BOOK_DETAIL",
        customTags = {"endpoint=getBookById", "single_resource=true"}
    )
    public ResponseEntity<ApiResponse<BookResponseDTO>> getBookById(@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(ApiResponse.success(bookService.getBookById(bookId)));
    }

    @GetMapping("/search")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.SEARCH,
        resourceType = "Book",
        logArguments = true,
        logReturnValue = false, // Search results can be large
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 3000L, // Search can be slower
        messagePrefix = "BOOK_SEARCH",
        customTags = {"endpoint=searchBooks", "search_operation=true", "full_text_search=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<BookResponseDTO>>> searchBooks(@RequestParam(value = "keyword") String keyword,
                                                                        @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(bookService.searchBooks(keyword, paginatedRequest)));
    }
    
    @PutMapping("/{bookId}")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.UPDATE,
        resourceType = "Book",
        logArguments = true,
        logReturnValue = true,
        performanceThresholdMs = 1000L,
        messagePrefix = "BOOK_UPDATE",
        customTags = {"endpoint=updateBook", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<BookResponseDTO>> updateBook(
            @PathVariable("bookId") Long bookId,
            @Valid @RequestBody BookCreateDTO bookUpdateDTO) {
        return ResponseEntity.ok(ApiResponse.success(bookService.updateBook(bookId, bookUpdateDTO)));
    }
    
    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.DELETE,
        resourceType = "Book",
        logArguments = true,
        logReturnValue = false,
        performanceThresholdMs = 1000L,
        messagePrefix = "BOOK_DELETE",
        customTags = {"endpoint=deleteBook", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable("bookId") Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Book",
        logArguments = false,
        logReturnValue = false,
        performanceThresholdMs = 2000L,
        messagePrefix = "ADMIN_DASHBOARD",
        customTags = {"endpoint=adminDashboard", "admin_operation=true"}
    )
    public ResponseEntity<ApiResponse<Object>> getAdminDashboard() {
        String currentUser = securityUtils.getCurrentUsername();
        // Implement admin dashboard logic
        return ResponseEntity.ok(ApiResponse.success(Map.of(
            "message", "Admin dashboard accessed by: " + currentUser,
            "timestamp", System.currentTimeMillis()
        )));
    }
}

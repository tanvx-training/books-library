package com.library.book.interfaces.rest;

import com.library.book.application.dto.request.BookCreateRequest;
import com.library.book.application.dto.response.BookResponse;
import com.library.book.application.service.BookApplicationService;
import com.library.book.application.service.UserContextService.UserContext;
import com.library.book.infrastructure.enums.LogLevel;
import com.library.book.infrastructure.enums.OperationType;
import com.library.book.infrastructure.logging.Loggable;
import com.library.book.application.dto.request.PaginatedRequest;
import com.library.book.application.dto.response.PaginatedResponse;
import com.library.book.application.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestBody @Valid BookCreateRequest bookCreateRequest,
            HttpServletRequest httpRequest) {
        
        UserContext userContext = (UserContext) httpRequest.getAttribute("userContext");
        if (userContext == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        return ResponseEntity.ok(ApiResponse.success(
                bookApplicationService.createBook(bookCreateRequest, userContext)
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
            @RequestBody @Valid BookCreateRequest bookUpdateRequest,
            HttpServletRequest httpRequest) {
        
        UserContext userContext = (UserContext) httpRequest.getAttribute("userContext");
        if (userContext == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        return ResponseEntity.ok(ApiResponse.success(
                bookApplicationService.updateBook(bookId, bookUpdateRequest, userContext)
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
            @PathVariable("bookId") Long bookId,
            HttpServletRequest httpRequest) {
        
        UserContext userContext = (UserContext) httpRequest.getAttribute("userContext");
        if (userContext == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        bookApplicationService.deleteBook(bookId, userContext);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
} 
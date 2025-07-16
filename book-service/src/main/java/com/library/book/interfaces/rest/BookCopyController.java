package com.library.book.interfaces.rest;

import com.library.book.application.dto.request.BookCopyCreateRequest;
import com.library.book.application.dto.request.PaginatedRequest;
import com.library.book.application.dto.response.BookCopyResponse;
import com.library.book.application.dto.response.PaginatedResponse;
import com.library.book.application.service.BookCopyApplicationService;
import com.library.book.application.service.UserContextService.UserContext;
import com.library.book.domain.service.BookCopyDomainService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for BookCopy operations
 */
@Slf4j
@RestController
@RequestMapping("/api/book-copies")
@RequiredArgsConstructor
public class BookCopyController {
    
    private final BookCopyApplicationService bookCopyApplicationService;
    
    @PostMapping
    public ResponseEntity<BookCopyResponse> createBookCopy(
            @Valid @RequestBody BookCopyCreateRequest request,
            HttpServletRequest httpRequest) {
        
        UserContext userContext = (UserContext) httpRequest.getAttribute("userContext");
        BookCopyResponse response = bookCopyApplicationService.createBookCopy(request, userContext);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/bulk")
    public ResponseEntity<List<BookCopyResponse>> createMultipleBookCopies(
            @RequestParam Long bookId,
            @RequestParam int numberOfCopies,
            @RequestParam(required = false) String locationPrefix,
            HttpServletRequest httpRequest) {
        
        UserContext userContext = (UserContext) httpRequest.getAttribute("userContext");
        List<BookCopyResponse> responses = bookCopyApplicationService.createMultipleBookCopies(
            bookId, numberOfCopies, locationPrefix, userContext);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BookCopyResponse> getBookCopyById(@PathVariable Long id) {
        BookCopyResponse response = bookCopyApplicationService.getBookCopyById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<PaginatedResponse<BookCopyResponse>> getAllBookCopies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PaginatedRequest paginatedRequest = PaginatedRequest.builder()
            .page(page)
            .size(size)
            .build();
        PaginatedResponse<BookCopyResponse> response = bookCopyApplicationService.getAllBookCopies(paginatedRequest);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BookCopyResponse>> getBookCopiesByBookId(@PathVariable Long bookId) {
        List<BookCopyResponse> responses = bookCopyApplicationService.getBookCopiesByBookId(bookId);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/my-borrowed")
    public ResponseEntity<List<BookCopyResponse>> getMyBorrowedBooks(HttpServletRequest httpRequest) {
        UserContext userContext = (UserContext) httpRequest.getAttribute("userContext");
        if (userContext == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<BookCopyResponse> responses = bookCopyApplicationService.getUserBorrowedBooks(userContext);
        return ResponseEntity.ok(responses);
    }
    
    @PostMapping("/{id}/borrow")
    public ResponseEntity<Map<String, String>> borrowBookCopy(
            @PathVariable Long id,
            @RequestParam(defaultValue = "14") int loanPeriodDays,
            HttpServletRequest httpRequest) {
        
        UserContext userContext = (UserContext) httpRequest.getAttribute("userContext");
        if (userContext == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        bookCopyApplicationService.borrowBookCopy(id, userContext, loanPeriodDays);
        
        return ResponseEntity.ok(Map.of(
            "message", "Book copy borrowed successfully",
            "bookCopyId", id.toString(),
            "borrower", userContext.getUsername()
        ));
    }
    
    @PostMapping("/{id}/return")
    public ResponseEntity<Map<String, String>> returnBookCopy(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        
        UserContext userContext = (UserContext) httpRequest.getAttribute("userContext");
        if (userContext == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        bookCopyApplicationService.returnBookCopy(id, userContext);
        
        return ResponseEntity.ok(Map.of(
            "message", "Book copy returned successfully",
            "bookCopyId", id.toString(),
            "returnedBy", userContext.getUsername()
        ));
    }
    
    @PostMapping("/{id}/reserve")
    public ResponseEntity<Map<String, String>> reserveBookCopy(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        
        UserContext userContext = (UserContext) httpRequest.getAttribute("userContext");
        if (userContext == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        bookCopyApplicationService.reserveBookCopy(id, userContext);
        
        return ResponseEntity.ok(Map.of(
            "message", "Book copy reserved successfully",
            "bookCopyId", id.toString(),
            "reserver", userContext.getUsername()
        ));
    }
    
    @GetMapping("/book/{bookId}/statistics")
    public ResponseEntity<BookCopyDomainService.BookCopyStatistics> getBookCopyStatistics(
            @PathVariable Long bookId) {
        
        BookCopyDomainService.BookCopyStatistics statistics = 
            bookCopyApplicationService.getBookCopyStatistics(bookId);
        
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "book-copy-service"
        ));
    }
}
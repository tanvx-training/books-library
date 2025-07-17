package com.library.book.interfaces.rest;

import com.library.book.application.dto.request.BookCopyCreateRequest;
import com.library.book.application.dto.request.PaginatedRequest;
import com.library.book.application.dto.response.BookCopyResponse;
import com.library.book.application.dto.response.PaginatedResponse;
import com.library.book.application.service.BookCopyApplicationService;
import com.library.book.domain.service.BookCopyDomainService;
import com.library.book.infrastructure.config.security.JwtAuthenticationService;
import com.library.book.infrastructure.config.security.RequireAuthentication;
import com.library.book.infrastructure.config.security.RequireBookManagement;
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
    private final JwtAuthenticationService jwtAuthenticationService;
    
    @PostMapping
    @RequireBookManagement
    public ResponseEntity<BookCopyResponse> createBookCopy(
            @Valid @RequestBody BookCopyCreateRequest request) {
        
        JwtAuthenticationService.AuthenticatedUser currentUser = jwtAuthenticationService.getCurrentUser();
        BookCopyResponse response = bookCopyApplicationService.createBookCopy(request, currentUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/bulk")
    @RequireBookManagement
    public ResponseEntity<List<BookCopyResponse>> createMultipleBookCopies(
            @RequestParam Long bookId,
            @RequestParam int numberOfCopies,
            @RequestParam(required = false) String locationPrefix) {
        
        JwtAuthenticationService.AuthenticatedUser currentUser = jwtAuthenticationService.getCurrentUser();
        List<BookCopyResponse> responses = bookCopyApplicationService.createMultipleBookCopies(
            bookId, numberOfCopies, locationPrefix, currentUser);
        
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
    @RequireAuthentication
    public ResponseEntity<List<BookCopyResponse>> getMyBorrowedBooks() {
        JwtAuthenticationService.AuthenticatedUser currentUser = jwtAuthenticationService.getCurrentUser();
        
        List<BookCopyResponse> responses = bookCopyApplicationService.getUserBorrowedBooks(currentUser);
        return ResponseEntity.ok(responses);
    }
    
    @PostMapping("/{id}/borrow")
    @RequireAuthentication
    public ResponseEntity<Map<String, String>> borrowBookCopy(
            @PathVariable Long id,
            @RequestParam(defaultValue = "14") int loanPeriodDays) {
        
        JwtAuthenticationService.AuthenticatedUser currentUser = jwtAuthenticationService.getCurrentUser();
        
        bookCopyApplicationService.borrowBookCopy(id, currentUser, loanPeriodDays);
        
        return ResponseEntity.ok(Map.of(
            "message", "Book copy borrowed successfully",
            "bookCopyId", id.toString(),
            "borrower", currentUser.getUsername()
        ));
    }
    
    @PostMapping("/{id}/return")
    @RequireAuthentication
    public ResponseEntity<Map<String, String>> returnBookCopy(@PathVariable Long id) {
        
        JwtAuthenticationService.AuthenticatedUser currentUser = jwtAuthenticationService.getCurrentUser();
        
        bookCopyApplicationService.returnBookCopy(id, currentUser);
        
        return ResponseEntity.ok(Map.of(
            "message", "Book copy returned successfully",
            "bookCopyId", id.toString(),
            "returnedBy", currentUser.getUsername()
        ));
    }
    
    @PostMapping("/{id}/reserve")
    @RequireAuthentication
    public ResponseEntity<Map<String, String>> reserveBookCopy(@PathVariable Long id) {
        
        JwtAuthenticationService.AuthenticatedUser currentUser = jwtAuthenticationService.getCurrentUser();
        
        bookCopyApplicationService.reserveBookCopy(id, currentUser);
        
        return ResponseEntity.ok(Map.of(
            "message", "Book copy reserved successfully",
            "bookCopyId", id.toString(),
            "reserver", currentUser.getUsername()
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
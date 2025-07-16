package com.library.book.interfaces.rest;

import com.library.book.application.dto.request.BookCopyCreateRequest;
import com.library.book.application.dto.request.PaginatedRequest;
import com.library.book.application.dto.response.BookCopyResponse;
import com.library.book.application.dto.response.PaginatedResponse;
import com.library.book.application.service.BookCopyApplicationService;
import com.library.book.application.service.UserContextService.UserContext;
import com.library.book.domain.service.BookCopyDomainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Book Copies", description = "Physical book copy management operations")
@Slf4j
@RestController
@RequestMapping("/api/book-copies")
@RequiredArgsConstructor
public class BookCopyController {
    
    private final BookCopyApplicationService bookCopyApplicationService;
    
    @Operation(
            summary = "Create a new book copy",
            description = "Creates a new physical copy of a book in the library system. Each copy has a unique identifier and location tracking.",
            tags = {"Book Copies"},
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Book copy created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookCopyResponse.class),
                            examples = @ExampleObject(
                                    name = "Created Book Copy",
                                    value = """
                                            {
                                              "id": 123,
                                              "bookId": 1,
                                              "copyNumber": "COPY-001",
                                              "status": "AVAILABLE",
                                              "condition": "NEW",
                                              "location": "A-1-001",
                                              "book": {
                                                "id": 1,
                                                "title": "The Great Gatsby",
                                                "isbn": "978-0-7432-7356-5"
                                              },
                                              "createdAt": "2024-01-15T10:30:00",
                                              "updatedAt": "2024-01-15T10:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid book copy data",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping
    public ResponseEntity<BookCopyResponse> createBookCopy(
            @Parameter(
                    description = "Book copy creation request",
                    required = true,
                    schema = @Schema(implementation = BookCopyCreateRequest.class)
            )
            @Valid @RequestBody BookCopyCreateRequest request,
            HttpServletRequest httpRequest) {
        
        UserContext userContext = (UserContext) httpRequest.getAttribute("userContext");
        BookCopyResponse response = bookCopyApplicationService.createBookCopy(request, userContext);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(
            summary = "Create multiple book copies in bulk",
            description = "Creates multiple physical copies of a book at once. Useful for adding new inventory to the library.",
            tags = {"Book Copies"},
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Book copies created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Bulk Created Copies",
                                    value = """
                                            [
                                              {
                                                "id": 123,
                                                "bookId": 1,
                                                "copyNumber": "COPY-001",
                                                "status": "AVAILABLE",
                                                "condition": "NEW",
                                                "location": "A-1-001"
                                              },
                                              {
                                                "id": 124,
                                                "bookId": 1,
                                                "copyNumber": "COPY-002",
                                                "status": "AVAILABLE",
                                                "condition": "NEW",
                                                "location": "A-1-002"
                                              }
                                            ]
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid parameters",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/bulk")
    public ResponseEntity<List<BookCopyResponse>> createMultipleBookCopies(
            @Parameter(description = "ID of the book to create copies for", required = true, example = "1")
            @RequestParam Long bookId,
            @Parameter(description = "Number of copies to create", required = true, example = "5")
            @RequestParam int numberOfCopies,
            @Parameter(description = "Location prefix for the copies", example = "A-1")
            @RequestParam(required = false) String locationPrefix,
            HttpServletRequest httpRequest) {
        
        UserContext userContext = (UserContext) httpRequest.getAttribute("userContext");
        List<BookCopyResponse> responses = bookCopyApplicationService.createMultipleBookCopies(
            bookId, numberOfCopies, locationPrefix, userContext);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
    
    @Operation(
            summary = "Get book copy by ID",
            description = "Retrieves detailed information about a specific book copy including its current status, condition, and location.",
            tags = {"Book Copies"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Book copy found successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookCopyResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Book copy not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookCopyResponse> getBookCopyById(
            @Parameter(description = "Unique identifier of the book copy", required = true, example = "123")
            @PathVariable Long id) {
        BookCopyResponse response = bookCopyApplicationService.getBookCopyById(id);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
            summary = "Get all book copies with pagination",
            description = "Retrieves a paginated list of all book copies in the library system with their current status and location information.",
            tags = {"Book Copies"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved book copies",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaginatedResponse.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<PaginatedResponse<BookCopyResponse>> getAllBookCopies(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        PaginatedRequest paginatedRequest = PaginatedRequest.builder()
            .page(page)
            .size(size)
            .build();
        PaginatedResponse<BookCopyResponse> response = bookCopyApplicationService.getAllBookCopies(paginatedRequest);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(
            summary = "Get all copies of a specific book",
            description = "Retrieves all physical copies of a specific book, showing their availability status, condition, and location.",
            tags = {"Book Copies"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved book copies",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Book Copies List",
                                    value = """
                                            [
                                              {
                                                "id": 123,
                                                "bookId": 1,
                                                "copyNumber": "COPY-001",
                                                "status": "AVAILABLE",
                                                "condition": "GOOD",
                                                "location": "A-1-001"
                                              },
                                              {
                                                "id": 124,
                                                "bookId": 1,
                                                "copyNumber": "COPY-002",
                                                "status": "BORROWED",
                                                "condition": "FAIR",
                                                "location": "A-1-002"
                                              }
                                            ]
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BookCopyResponse>> getBookCopiesByBookId(
            @Parameter(description = "Unique identifier of the book", required = true, example = "1")
            @PathVariable Long bookId) {
        List<BookCopyResponse> responses = bookCopyApplicationService.getBookCopiesByBookId(bookId);
        return ResponseEntity.ok(responses);
    }
    
    @Operation(
            summary = "Get current user's borrowed books",
            description = "Retrieves all book copies currently borrowed by the authenticated user.",
            tags = {"Book Copies"},
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved borrowed books",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Borrowed Books",
                                    value = """
                                            [
                                              {
                                                "id": 123,
                                                "bookId": 1,
                                                "copyNumber": "COPY-001",
                                                "status": "BORROWED",
                                                "condition": "GOOD",
                                                "location": "A-1-001",
                                                "borrowedAt": "2024-01-15T10:30:00",
                                                "dueDate": "2024-01-29T10:30:00",
                                                "book": {
                                                  "id": 1,
                                                  "title": "The Great Gatsby",
                                                  "isbn": "978-0-7432-7356-5"
                                                }
                                              }
                                            ]
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/my-borrowed")
    public ResponseEntity<List<BookCopyResponse>> getMyBorrowedBooks(HttpServletRequest httpRequest) {
        UserContext userContext = (UserContext) httpRequest.getAttribute("userContext");
        if (userContext == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<BookCopyResponse> responses = bookCopyApplicationService.getUserBorrowedBooks(userContext);
        return ResponseEntity.ok(responses);
    }
    
    @Operation(
            summary = "Borrow a book copy",
            description = "Allows an authenticated user to borrow an available book copy. The copy status will be changed to BORROWED.",
            tags = {"Book Copies"},
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Book copy borrowed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Borrow Success",
                                    value = """
                                            {
                                              "message": "Book copy borrowed successfully",
                                              "bookCopyId": "123",
                                              "borrower": "john.doe"
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Book copy not available for borrowing",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Book copy not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/{id}/borrow")
    public ResponseEntity<Map<String, String>> borrowBookCopy(
            @Parameter(description = "ID of the book copy to borrow", required = true, example = "123")
            @PathVariable Long id,
            @Parameter(description = "Loan period in days", example = "14")
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
    
    @Operation(
            summary = "Return a borrowed book copy",
            description = "Allows an authenticated user to return a book copy they have borrowed. The copy status will be changed back to AVAILABLE.",
            tags = {"Book Copies"},
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Book copy returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Return Success",
                                    value = """
                                            {
                                              "message": "Book copy returned successfully",
                                              "bookCopyId": "123",
                                              "returnedBy": "john.doe"
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Book copy not borrowed by this user or already returned",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Book copy not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/{id}/return")
    public ResponseEntity<Map<String, String>> returnBookCopy(
            @Parameter(description = "ID of the book copy to return", required = true, example = "123")
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
    
    @Operation(
            summary = "Reserve a book copy",
            description = "Allows an authenticated user to reserve an available book copy. The copy status will be changed to RESERVED.",
            tags = {"Book Copies"},
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Book copy reserved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Reserve Success",
                                    value = """
                                            {
                                              "message": "Book copy reserved successfully",
                                              "bookCopyId": "123",
                                              "reserver": "john.doe"
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Book copy not available for reservation",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Book copy not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/{id}/reserve")
    public ResponseEntity<Map<String, String>> reserveBookCopy(
            @Parameter(description = "ID of the book copy to reserve", required = true, example = "123")
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
    
    @Operation(
            summary = "Get book copy statistics",
            description = "Retrieves comprehensive statistics about all copies of a specific book including availability, borrowing patterns, and condition reports.",
            tags = {"Book Copies"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved statistics",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Book Copy Statistics",
                                    value = """
                                            {
                                              "bookId": 1,
                                              "totalCopies": 10,
                                              "availableCopies": 6,
                                              "borrowedCopies": 3,
                                              "reservedCopies": 1,
                                              "maintenanceCopies": 0,
                                              "conditionBreakdown": {
                                                "NEW": 2,
                                                "GOOD": 5,
                                                "FAIR": 2,
                                                "POOR": 1
                                              },
                                              "borrowingRate": 0.3,
                                              "averageLoanDuration": 12.5
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/book/{bookId}/statistics")
    public ResponseEntity<BookCopyDomainService.BookCopyStatistics> getBookCopyStatistics(
            @Parameter(description = "Unique identifier of the book", required = true, example = "1")
            @PathVariable Long bookId) {
        
        BookCopyDomainService.BookCopyStatistics statistics = 
            bookCopyApplicationService.getBookCopyStatistics(bookId);
        
        return ResponseEntity.ok(statistics);
    }
    
    @Operation(
            summary = "Health check endpoint",
            description = "Simple health check endpoint to verify the book copy service is running and responsive.",
            tags = {"Book Copies"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Service is healthy",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Health Status",
                                    value = """
                                            {
                                              "status": "UP",
                                              "service": "book-copy-service"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "book-copy-service"
        ));
    }
}
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

@Tag(name = "Books", description = "Book management operations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookApplicationService bookApplicationService;

    @Operation(
            summary = "Get all books with pagination",
            description = "Retrieves a paginated list of all books in the library system. " +
                         "Supports sorting by various fields and filtering options.",
            tags = {"Books"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved books",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = """
                                            {
                                              "status": 1,
                                              "message": "Success",
                                              "data": {
                                                "content": [
                                                  {
                                                    "id": 1,
                                                    "title": "The Great Gatsby",
                                                    "isbn": "978-0-7432-7356-5",
                                                    "publicationYear": 1925,
                                                    "description": "A classic American novel",
                                                    "publisher": {
                                                      "id": 1,
                                                      "name": "Scribner"
                                                    },
                                                    "authors": [
                                                      {
                                                        "id": 1,
                                                        "name": "F. Scott Fitzgerald"
                                                      }
                                                    ],
                                                    "categories": [
                                                      {
                                                        "id": 1,
                                                        "name": "Fiction"
                                                      }
                                                    ]
                                                  }
                                                ],
                                                "totalElements": 100,
                                                "totalPages": 10,
                                                "currentPage": 0,
                                                "pageSize": 10
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
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
            @Parameter(
                    description = "Pagination parameters",
                    required = true,
                    schema = @Schema(implementation = PaginatedRequest.class)
            )
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                bookApplicationService.getAllBooks(paginatedRequest)
        ));
    }

    @Operation(
            summary = "Get book by ID",
            description = "Retrieves detailed information about a specific book including its authors, publisher, categories, and metadata.",
            tags = {"Books"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Book found successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Book Details",
                                    value = """
                                            {
                                              "status": 1,
                                              "message": "Success",
                                              "data": {
                                                "id": 1,
                                                "title": "The Great Gatsby",
                                                "isbn": "978-0-7432-7356-5",
                                                "publicationYear": 1925,
                                                "description": "A classic American novel set in the Jazz Age",
                                                "coverImageUrl": "https://example.com/covers/gatsby.jpg",
                                                "publisher": {
                                                  "id": 1,
                                                  "name": "Scribner",
                                                  "address": "New York, NY"
                                                },
                                                "authors": [
                                                  {
                                                    "id": 1,
                                                    "name": "F. Scott Fitzgerald",
                                                    "biography": "American novelist and short story writer"
                                                  }
                                                ],
                                                "categories": [
                                                  {
                                                    "id": 1,
                                                    "name": "Fiction",
                                                    "slug": "fiction"
                                                  }
                                                ],
                                                "createdAt": "2024-01-15T10:30:00",
                                                "updatedAt": "2024-01-15T10:30:00"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Book Not Found",
                                    value = """
                                            {
                                              "status": 0,
                                              "message": "Book not found with ID: 999",
                                              "error": {
                                                "code": 404,
                                                "message": "Book not found with ID: 999"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
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
            @Parameter(
                    description = "Unique identifier of the book",
                    required = true,
                    example = "1"
            )
            @PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(ApiResponse.success(
                bookApplicationService.getBookById(bookId)
        ));
    }

    @Operation(
            summary = "Search books by keyword",
            description = "Performs a comprehensive search across book titles, authors, descriptions, and ISBN. " +
                         "Supports pagination and returns ranked results based on relevance.",
            tags = {"Books", "Search"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Search Results",
                                    value = """
                                            {
                                              "status": 1,
                                              "message": "Success",
                                              "data": {
                                                "content": [
                                                  {
                                                    "id": 1,
                                                    "title": "The Great Gatsby",
                                                    "isbn": "978-0-7432-7356-5",
                                                    "publicationYear": 1925,
                                                    "description": "A classic American novel",
                                                    "publisher": {"id": 1, "name": "Scribner"},
                                                    "authors": [{"id": 1, "name": "F. Scott Fitzgerald"}]
                                                  }
                                                ],
                                                "totalElements": 5,
                                                "totalPages": 1,
                                                "currentPage": 0,
                                                "pageSize": 10
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid search parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
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
            @Parameter(
                    description = "Search keyword to match against book title, author name, description, or ISBN",
                    required = true,
                    example = "gatsby"
            )
            @RequestParam("keyword") String keyword,
            @Parameter(
                    description = "Pagination parameters for search results",
                    required = true,
                    schema = @Schema(implementation = PaginatedRequest.class)
            )
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                bookApplicationService.searchBooks(keyword, paginatedRequest)
        ));
    }

    @Operation(
            summary = "Create a new book",
            description = "Creates a new book in the library system with complete metadata including authors, publisher, and categories. " +
                         "Requires authentication and appropriate permissions.",
            tags = {"Books"},
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Book created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Created Book",
                                    value = """
                                            {
                                              "status": 1,
                                              "message": "Success",
                                              "data": {
                                                "id": 123,
                                                "title": "New Book Title",
                                                "isbn": "978-1-234-56789-0",
                                                "publicationYear": 2024,
                                                "description": "A fascinating new book",
                                                "publisher": {"id": 1, "name": "Publisher Name"},
                                                "authors": [{"id": 1, "name": "Author Name"}],
                                                "categories": [{"id": 1, "name": "Category Name"}],
                                                "createdAt": "2024-01-15T10:30:00",
                                                "updatedAt": "2024-01-15T10:30:00"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid book data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Validation Error",
                                    value = """
                                            {
                                              "status": 0,
                                              "message": "Validation failed",
                                              "error": {
                                                "code": 400,
                                                "message": "Title must not be blank",
                                                "validationErrors": [
                                                  {
                                                    "field": "title",
                                                    "message": "Title must not be blank"
                                                  }
                                                ]
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Book with same ISBN already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
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
            @Parameter(
                    description = "Book creation request with all required fields",
                    required = true,
                    schema = @Schema(implementation = BookCreateRequest.class)
            )
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

    @Operation(
            summary = "Update an existing book",
            description = "Updates an existing book's information including metadata, authors, publisher, and categories. " +
                         "Requires authentication and appropriate permissions.",
            tags = {"Books"},
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Book updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid book data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
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
            @Parameter(
                    description = "Unique identifier of the book to update",
                    required = true,
                    example = "1"
            )
            @PathVariable("bookId") Long bookId,
            @Parameter(
                    description = "Updated book information",
                    required = true,
                    schema = @Schema(implementation = BookCreateRequest.class)
            )
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

    @Operation(
            summary = "Delete a book",
            description = "Soft deletes a book from the library system. The book will be marked as deleted but not physically removed. " +
                         "Requires authentication and appropriate permissions.",
            tags = {"Books"},
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Book deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Deletion Success",
                                    value = """
                                            {
                                              "status": 1,
                                              "message": "Success",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Cannot delete book with active copies or reservations",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
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
            @Parameter(
                    description = "Unique identifier of the book to delete",
                    required = true,
                    example = "1"
            )
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
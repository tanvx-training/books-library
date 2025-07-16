package com.library.book.interfaces.rest;

import com.library.book.application.dto.request.AuthorCreateRequest;
import com.library.book.application.dto.response.AuthorResponse;
import com.library.book.application.service.AuthorApplicationService;
import com.library.book.application.service.UserContextService;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authors", description = "Author management operations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/authors")
public class AuthorController {

    private final AuthorApplicationService authorApplicationService;

    @Operation(
            summary = "Get all authors with pagination",
            description = "Retrieves a paginated list of all authors in the library system with their biographical information and book associations.",
            tags = {"Authors"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved authors",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Authors List",
                                    value = """
                                            {
                                              "status": 1,
                                              "message": "Success",
                                              "data": {
                                                "content": [
                                                  {
                                                    "id": 1,
                                                    "name": "F. Scott Fitzgerald",
                                                    "biography": "American novelist and short story writer",
                                                    "bookCount": 5,
                                                    "createdAt": "2024-01-15T10:30:00"
                                                  }
                                                ],
                                                "totalElements": 50,
                                                "totalPages": 5,
                                                "currentPage": 0,
                                                "pageSize": 10
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    @Loggable(
            level = LogLevel.BASIC,
            operationType = OperationType.READ,
            resourceType = "Author",
            logReturnValue = false,
            messagePrefix = "AUTHOR_LIST",
            customTags = {"endpoint=getAllAuthors", "pagination=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<AuthorResponse>>> getAllAuthors(
            @Parameter(
                    description = "Pagination parameters",
                    required = true,
                    schema = @Schema(implementation = PaginatedRequest.class)
            )
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                authorApplicationService.getAllAuthors(paginatedRequest)
        ));
    }

    @Operation(
            summary = "Get author by ID",
            description = "Retrieves detailed information about a specific author including biography and associated books.",
            tags = {"Authors"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Author found successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Author Details",
                                    value = """
                                            {
                                              "status": 1,
                                              "message": "Success",
                                              "data": {
                                                "id": 1,
                                                "name": "F. Scott Fitzgerald",
                                                "biography": "American novelist and short story writer, widely regarded as one of the greatest American writers of the 20th century.",
                                                "bookCount": 5,
                                                "books": [
                                                  {
                                                    "id": 1,
                                                    "title": "The Great Gatsby",
                                                    "isbn": "978-0-7432-7356-5"
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
                    description = "Author not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    @GetMapping("/{authorId}")
    @Loggable(
            level = LogLevel.BASIC,
            operationType = OperationType.READ,
            resourceType = "Author",
            performanceThresholdMs = 500L,
            messagePrefix = "AUTHOR_DETAIL",
            customTags = {"endpoint=getAuthorById"}
    )
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthorById(
            @Parameter(
                    description = "Unique identifier of the author",
                    required = true,
                    example = "1"
            )
            @PathVariable("authorId") Long authorId) {
        return ResponseEntity.ok(ApiResponse.success(
                authorApplicationService.getAuthorById(authorId)
        ));
    }

    @Operation(
            summary = "Create a new author",
            description = "Creates a new author profile in the library system. Requires authentication and appropriate permissions.",
            tags = {"Authors"},
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Author created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Created Author",
                                    value = """
                                            {
                                              "status": 1,
                                              "message": "Success",
                                              "data": {
                                                "id": 123,
                                                "name": "New Author Name",
                                                "biography": "Biography of the new author",
                                                "bookCount": 0,
                                                "books": [],
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
                    description = "Invalid author data",
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
                    responseCode = "409",
                    description = "Author with same name already exists",
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
            resourceType = "Author",
            performanceThresholdMs = 2000L,
            messagePrefix = "AUTHOR_CREATION",
            customTags = {"endpoint=createAuthor", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<AuthorResponse>> createAuthor(
            @Parameter(
                    description = "Author creation request with name and biography",
                    required = true,
                    schema = @Schema(implementation = AuthorCreateRequest.class)
            )
            @RequestBody @Valid AuthorCreateRequest authorCreateRequest,
            HttpServletRequest httpRequest) {
        
        UserContextService.UserContext userContext = (UserContextService.UserContext) httpRequest.getAttribute("userContext");
        if (userContext == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        return ResponseEntity.ok(ApiResponse.success(
                authorApplicationService.createAuthor(authorCreateRequest, userContext)
        ));
    }
}
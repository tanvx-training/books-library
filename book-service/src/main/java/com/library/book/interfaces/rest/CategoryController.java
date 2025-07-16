package com.library.book.interfaces.rest;

import com.library.book.application.dto.request.CategoryCreateRequest;
import com.library.book.application.dto.response.CategoryResponse;
import com.library.book.application.service.CategoryApplicationService;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Categories", description = "Category management operations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryApplicationService categoryApplicationService;

    @Operation(
            summary = "Get all categories with pagination",
            description = "Retrieves a paginated list of all book categories in the library system. " +
                         "Categories can be hierarchical with parent-child relationships.",
            tags = {"Categories"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved categories",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Categories List",
                                    value = """
                                            {
                                              "status": 1,
                                              "message": "Success",
                                              "data": {
                                                "content": [
                                                  {
                                                    "id": 1,
                                                    "name": "Fiction",
                                                    "slug": "fiction",
                                                    "description": "Fictional literature and novels",
                                                    "bookCount": 150,
                                                    "parentCategoryId": null,
                                                    "childCategories": [
                                                      {
                                                        "id": 2,
                                                        "name": "Science Fiction",
                                                        "slug": "science-fiction"
                                                      }
                                                    ],
                                                    "createdAt": "2024-01-15T10:30:00"
                                                  }
                                                ],
                                                "totalElements": 25,
                                                "totalPages": 3,
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
            resourceType = "Category",
            logArguments = true,
            logReturnValue = false,
            performanceThresholdMs = 1000L,
            messagePrefix = "CATEGORY_LIST",
            customTags = {"endpoint=getAllCategories", "pagination=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<CategoryResponse>>> getAllCategories(
            @Parameter(
                    description = "Pagination parameters",
                    required = true,
                    schema = @Schema(implementation = PaginatedRequest.class)
            )
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                categoryApplicationService.getAllCategories(paginatedRequest)
        ));
    }

    @Operation(
            summary = "Get category by ID",
            description = "Retrieves detailed information about a specific category including its hierarchical relationships and associated books.",
            tags = {"Categories"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Category found successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Category Details",
                                    value = """
                                            {
                                              "status": 1,
                                              "message": "Success",
                                              "data": {
                                                "id": 1,
                                                "name": "Fiction",
                                                "slug": "fiction",
                                                "description": "Fictional literature including novels, short stories, and novellas",
                                                "bookCount": 150,
                                                "parentCategoryId": null,
                                                "childCategories": [
                                                  {
                                                    "id": 2,
                                                    "name": "Science Fiction",
                                                    "slug": "science-fiction",
                                                    "bookCount": 45
                                                  },
                                                  {
                                                    "id": 3,
                                                    "name": "Fantasy",
                                                    "slug": "fantasy",
                                                    "bookCount": 38
                                                  }
                                                ],
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
                    description = "Category not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    @GetMapping("/{categoryId}")
    @Loggable(
            level = LogLevel.BASIC,
            operationType = OperationType.READ,
            resourceType = "Category",
            logArguments = true,
            logReturnValue = true,
            performanceThresholdMs = 500L,
            messagePrefix = "CATEGORY_DETAIL",
            customTags = {"endpoint=getCategoryById"}
    )
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @Parameter(
                    description = "Unique identifier of the category",
                    required = true,
                    example = "1"
            )
            @PathVariable("categoryId") Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success(
                categoryApplicationService.getCategoryById(categoryId)
        ));
    }

    @Operation(
            summary = "Create a new category",
            description = "Creates a new book category in the library system. Categories can be hierarchical with parent-child relationships. " +
                         "Requires authentication and appropriate permissions.",
            tags = {"Categories"},
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Category created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Created Category",
                                    value = """
                                            {
                                              "status": 1,
                                              "message": "Success",
                                              "data": {
                                                "id": 123,
                                                "name": "New Category",
                                                "slug": "new-category",
                                                "description": "Description of the new category",
                                                "bookCount": 0,
                                                "parentCategoryId": null,
                                                "childCategories": [],
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
                    description = "Invalid category data",
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
                                                "message": "Category name must not be blank",
                                                "validationErrors": [
                                                  {
                                                    "field": "name",
                                                    "message": "Category name must not be blank"
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
                    description = "Category with same name or slug already exists",
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
            resourceType = "Category",
            logArguments = true,
            logReturnValue = true,
            performanceThresholdMs = 2000L,
            messagePrefix = "CATEGORY_CREATION",
            customTags = {"endpoint=createCategory", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Parameter(
                    description = "Category creation request with name, slug, and description",
                    required = true,
                    schema = @Schema(implementation = CategoryCreateRequest.class)
            )
            @RequestBody @Valid CategoryCreateRequest categoryCreateRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                categoryApplicationService.createCategory(categoryCreateRequest)
        ));
    }
}
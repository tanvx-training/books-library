package com.library.book.interfaces.rest;

import com.library.book.application.dto.request.PublisherCreateRequest;
import com.library.book.application.dto.response.PublisherResponse;
import com.library.book.application.service.PublisherApplicationService;
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

@Tag(name = "Publishers", description = "Publisher management operations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/publishers")
public class PublisherController {

    private final PublisherApplicationService publisherApplicationService;

    @Operation(
            summary = "Get all publishers with pagination",
            description = "Retrieves a paginated list of all publishers in the library system with their contact information and book associations.",
            tags = {"Publishers"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved publishers",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Publishers List",
                                    value = """
                                            {
                                              "status": 1,
                                              "message": "Success",
                                              "data": {
                                                "content": [
                                                  {
                                                    "id": 1,
                                                    "name": "Penguin Random House",
                                                    "address": "1745 Broadway, New York, NY 10019",
                                                    "contactInfo": {
                                                      "email": "info@penguinrandomhouse.com",
                                                      "phone": "+1-212-782-9000",
                                                      "website": "https://www.penguinrandomhouse.com"
                                                    },
                                                    "establishedDate": "2013-07-01T00:00:00",
                                                    "bookCount": 1250,
                                                    "createdAt": "2024-01-15T10:30:00"
                                                  }
                                                ],
                                                "totalElements": 75,
                                                "totalPages": 8,
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
            resourceType = "Publisher",
            logReturnValue = false,
            messagePrefix = "PUBLISHER_LIST",
            customTags = {"endpoint=getAllPublishers", "pagination=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<PublisherResponse>>> getAllPublishers(
            @Parameter(
                    description = "Pagination parameters",
                    required = true,
                    schema = @Schema(implementation = PaginatedRequest.class)
            )
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                publisherApplicationService.getAllPublishers(paginatedRequest)
        ));
    }

    @Operation(
            summary = "Get publisher by ID",
            description = "Retrieves detailed information about a specific publisher including contact information, establishment date, and associated books.",
            tags = {"Publishers"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Publisher found successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Publisher Details",
                                    value = """
                                            {
                                              "status": 1,
                                              "message": "Success",
                                              "data": {
                                                "id": 1,
                                                "name": "Penguin Random House",
                                                "address": "1745 Broadway, New York, NY 10019",
                                                "contactInfo": {
                                                  "email": "info@penguinrandomhouse.com",
                                                  "phone": "+1-212-782-9000",
                                                  "website": "https://www.penguinrandomhouse.com"
                                                },
                                                "establishedDate": "2013-07-01T00:00:00",
                                                "bookCount": 1250,
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
                    description = "Publisher not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    @GetMapping("/{publisherId}")
    @Loggable(
            level = LogLevel.BASIC,
            operationType = OperationType.READ,
            resourceType = "Publisher",
            messagePrefix = "PUBLISHER_DETAIL",
            customTags = {"endpoint=getPublisherById"}
    )
    public ResponseEntity<ApiResponse<PublisherResponse>> getPublisherById(
            @Parameter(
                    description = "Unique identifier of the publisher",
                    required = true,
                    example = "1"
            )
            @PathVariable("publisherId") Long publisherId) {
        return ResponseEntity.ok(ApiResponse.success(
                publisherApplicationService.getPublisherById(publisherId)
        ));
    }

    @Operation(
            summary = "Create a new publisher",
            description = "Creates a new publisher in the library system with complete contact information and establishment details. " +
                         "Requires authentication and appropriate permissions.",
            tags = {"Publishers"},
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Publisher created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Created Publisher",
                                    value = """
                                            {
                                              "status": 1,
                                              "message": "Success",
                                              "data": {
                                                "id": 123,
                                                "name": "New Publishing House",
                                                "address": "123 Main Street, City, State 12345",
                                                "contactInfo": {
                                                  "email": "contact@newpublisher.com",
                                                  "phone": "+1-555-123-4567",
                                                  "website": "https://www.newpublisher.com"
                                                },
                                                "establishedDate": "2024-01-15T00:00:00",
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
                    description = "Invalid publisher data",
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
                                                "message": "Publisher name must not be blank",
                                                "validationErrors": [
                                                  {
                                                    "field": "name",
                                                    "message": "Publisher name must not be blank"
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
                    description = "Publisher with same name already exists",
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
            resourceType = "Publisher",
            performanceThresholdMs = 2000L,
            messagePrefix = "PUBLISHER_CREATION",
            customTags = {"endpoint=createPublisher", "content_management=true"}
    )
    public ResponseEntity<ApiResponse<PublisherResponse>> createPublisher(
            @Parameter(
                    description = "Publisher creation request with name, address, and contact information",
                    required = true,
                    schema = @Schema(implementation = PublisherCreateRequest.class)
            )
            @RequestBody @Valid PublisherCreateRequest publisherCreateRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                publisherApplicationService.createPublisher(publisherCreateRequest)
        ));
    }
}
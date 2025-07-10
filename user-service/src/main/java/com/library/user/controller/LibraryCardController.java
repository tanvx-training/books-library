package com.library.user.controller;

import com.library.common.aop.annotation.Loggable;
import com.library.common.dto.ApiResponse;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import com.library.user.infrastructure.enums.LibraryCardStatus;
import com.library.user.service.LibraryCardService;
import com.library.user.dto.request.CreateLibraryCardRequestDTO;
import com.library.user.dto.request.RenewLibraryCardRequestDTO;
import com.library.user.dto.request.UpdateLibraryCardStatusRequestDTO;
import com.library.user.dto.response.LibraryCardResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/library-cards")
public class LibraryCardController {

    private final LibraryCardService libraryCardService;

    /**
     * Create a new library card
     * @param requestDTO the request
     * @return the created library card
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.CREATE,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "LIBRARY_CARD_CREATION",
        customTags = {
            "endpoint=createLibraryCard", 
            "security_operation=true", 
            "admin_operation=true",
            "card_management=true"
        }
    )
    public ResponseEntity<ApiResponse<LibraryCardResponseDTO>> createLibraryCard(@Valid @RequestBody CreateLibraryCardRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(libraryCardService.createLibraryCard(requestDTO)));
    }
    
    /**
     * Get a library card by ID
     * @param id the library card ID
     * @return the library card
     */
    @GetMapping("/{id}")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = true,
        performanceThresholdMs = 500L,
        messagePrefix = "LIBRARY_CARD_DETAIL",
        customTags = {"endpoint=getLibraryCardById", "single_resource=true", "card_lookup=true"}
    )
    public ResponseEntity<ApiResponse<LibraryCardResponseDTO>> getLibraryCardById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(libraryCardService.getLibraryCardById(id)));
    }
    
    /**
     * Get all library cards for a user
     * @param userId the user ID
     * @return list of library cards
     */
    @GetMapping("/user/{userId}")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = false, // Don't log card lists - can contain sensitive info
        performanceThresholdMs = 1000L,
        messagePrefix = "LIBRARY_CARD_BY_USER",
        customTags = {
            "endpoint=getLibraryCardsByUserId", 
            "relationship_query=true", 
            "user_cards_lookup=true"
        }
    )
    public ResponseEntity<ApiResponse<List<LibraryCardResponseDTO>>> getLibraryCardsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(libraryCardService.getLibraryCardsByUserId(userId)));
    }
    
    /**
     * Get all library cards with optional status filter
     * @param status the status to filter by (optional)
     * @return list of library cards
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = false, // Don't log all cards - can be large and sensitive
        performanceThresholdMs = 1500L,
        messagePrefix = "LIBRARY_CARD_LIST",
        customTags = {
            "endpoint=getAllLibraryCards", 
            "admin_operation=true", 
            "status_filter=true",
            "card_management=true"
        }
    )
    public ResponseEntity<ApiResponse<List<LibraryCardResponseDTO>>> getAllLibraryCards(
            @RequestParam(required = false) LibraryCardStatus status) {
        return ResponseEntity.ok(ApiResponse.success(libraryCardService.getAllLibraryCards(status)));
    }
    
    /**
     * Update the status of a library card
     * @param id the library card ID
     * @param requestDTO the request
     * @return the updated library card
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 1500L,
        messagePrefix = "LIBRARY_CARD_STATUS_UPDATE",
        customTags = {
            "endpoint=updateLibraryCardStatus", 
            "admin_operation=true", 
            "status_change=true",
            "business_critical=true",
            "card_management=true",
            "audit_required=true"
        }
    )
    public ResponseEntity<ApiResponse<LibraryCardResponseDTO>> updateLibraryCardStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLibraryCardStatusRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(libraryCardService.updateLibraryCardStatus(id, requestDTO)));
    }
    
    /**
     * Renew a library card with a new expiry date
     * @param id the library card ID
     * @param requestDTO the request
     * @return the renewed library card
     */
    @PatchMapping("/{id}/renew")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "LIBRARY_CARD_RENEWAL",
        customTags = {
            "endpoint=renewLibraryCard", 
            "admin_operation=true", 
            "renewal_operation=true",
            "business_critical=true",
            "card_management=true",
            "expiry_update=true",
            "audit_required=true"
        }
    )
    public ResponseEntity<ApiResponse<LibraryCardResponseDTO>> renewLibraryCard(
            @PathVariable Long id,
            @Valid @RequestBody RenewLibraryCardRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(libraryCardService.renewLibraryCard(id, requestDTO)));
    }
}

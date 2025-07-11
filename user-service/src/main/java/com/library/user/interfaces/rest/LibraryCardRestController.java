package com.library.user.interfaces.rest;

import com.library.user.application.dto.response.ApiResponse;
import com.library.user.application.dto.response.LibraryCardResponse;
import com.library.user.application.service.LibraryCardApplicationService;
import com.library.user.infrastructure.enums.LogLevel;
import com.library.user.infrastructure.enums.OperationType;
import com.library.user.infrastructure.logging.Loggable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/library-cards")
public class LibraryCardRestController {

    private final LibraryCardApplicationService libraryCardApplicationService;

    @GetMapping("/users/{userId}")
    @Loggable(
            level = LogLevel.BASIC,
            operationType = OperationType.READ,
            resourceType = "LibraryCard",
            logReturnValue = false,
            messagePrefix = "LIBRARY_CARD_API_LIST",
            customTags = {"layer=interface", "endpoint=getLibraryCardsForUser"}
    )
    public ResponseEntity<ApiResponse<List<LibraryCardResponse>>> getLibraryCardsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(libraryCardApplicationService.getLibraryCardsForUser(userId)));
    }

    @PostMapping("/users/{userId}")
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.CREATE,
            resourceType = "LibraryCard",
            performanceThresholdMs = 2000L,
            messagePrefix = "LIBRARY_CARD_API_CREATE",
            customTags = {"layer=interface", "endpoint=createLibraryCard"}
    )
    public ResponseEntity<ApiResponse<LibraryCardResponse>> createLibraryCard(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(libraryCardApplicationService.createLibraryCard(userId)));
    }

    @PutMapping("/{cardId}/renew")
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.UPDATE,
            resourceType = "LibraryCard",
            performanceThresholdMs = 2000L,
            messagePrefix = "LIBRARY_CARD_API_RENEW",
            customTags = {"layer=interface", "endpoint=renewLibraryCard"}
    )
    public ResponseEntity<ApiResponse<LibraryCardResponse>> renewLibraryCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(ApiResponse.success(libraryCardApplicationService.renewLibraryCard(cardId)));
    }
}
package com.library.member.controller;

import com.library.member.service.LibraryCardService;
import com.library.member.dto.request.CreateLibraryCardRequest;
import com.library.member.dto.request.UpdateCardStatusRequest;
import com.library.member.dto.response.LibraryCardResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members/library-cards")
@RequiredArgsConstructor
public class LibraryCardController {
    
    private static final Logger logger = LoggerFactory.getLogger(LibraryCardController.class);
    
    private final LibraryCardService libraryCardService;

    @PostMapping
    public ResponseEntity<LibraryCardResponse> createLibraryCard(@Valid @RequestBody CreateLibraryCardRequest createRequest) {
        LibraryCardResponse libraryCardResponse = libraryCardService.createLibraryCard(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryCardResponse);
    }

    @GetMapping("/my-cards")
    public ResponseEntity<List<LibraryCardResponse>> getMyLibraryCards() {
        List<LibraryCardResponse> libraryCards = libraryCardService.getCurrentUserLibraryCards();
        return ResponseEntity.ok(libraryCards);
    }

    @GetMapping("/user/{keycloakId}")
    public ResponseEntity<List<LibraryCardResponse>> getUserLibraryCards(@PathVariable String keycloakId) {
        List<LibraryCardResponse> libraryCards = libraryCardService.getUserLibraryCards(keycloakId);
        return ResponseEntity.ok(libraryCards);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<LibraryCardResponse> getLibraryCard(@PathVariable UUID cardId) {
        LibraryCardResponse libraryCard = libraryCardService.getLibraryCard(cardId);
        return ResponseEntity.ok(libraryCard);
    }

    @PutMapping("/{cardId}/status")
    public ResponseEntity<LibraryCardResponse> updateCardStatus(
            @PathVariable UUID cardId, 
            @Valid @RequestBody UpdateCardStatusRequest updateRequest) {
        LibraryCardResponse libraryCard = libraryCardService.updateLibraryCardStatus(cardId, updateRequest);
        return ResponseEntity.ok(libraryCard);
    }

    @GetMapping("/user/{keycloakId}/active")
    public ResponseEntity<List<LibraryCardResponse>> getUserActiveLibraryCards(@PathVariable String keycloakId) {
        List<LibraryCardResponse> activeCards = libraryCardService.getUserActiveLibraryCards(keycloakId);
        return ResponseEntity.ok(activeCards);
    }
}
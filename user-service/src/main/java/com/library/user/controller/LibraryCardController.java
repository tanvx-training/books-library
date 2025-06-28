package com.library.user.controller;

import com.library.common.dto.ApiResponse;
import com.library.user.utils.enums.LibraryCardStatus;
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
    public ResponseEntity<ApiResponse<LibraryCardResponseDTO>> createLibraryCard(@Valid @RequestBody CreateLibraryCardRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(libraryCardService.createLibraryCard(requestDTO)));
    }
    
    /**
     * Get a library card by ID
     * @param id the library card ID
     * @return the library card
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LibraryCardResponseDTO>> getLibraryCardById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(libraryCardService.getLibraryCardById(id)));
    }
    
    /**
     * Get all library cards for a user
     * @param userId the user ID
     * @return list of library cards
     */
    @GetMapping("/user/{userId}")
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
    public ResponseEntity<ApiResponse<LibraryCardResponseDTO>> renewLibraryCard(
            @PathVariable Long id,
            @Valid @RequestBody RenewLibraryCardRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(libraryCardService.renewLibraryCard(id, requestDTO)));
    }
}

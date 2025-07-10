package com.library.user.service;

import com.library.user.infrastructure.enums.LibraryCardStatus;
import com.library.user.dto.request.CreateLibraryCardRequestDTO;
import com.library.user.dto.request.RenewLibraryCardRequestDTO;
import com.library.user.dto.request.UpdateLibraryCardStatusRequestDTO;
import com.library.user.dto.response.LibraryCardResponseDTO;

import java.util.List;

public interface LibraryCardService {

    /**
     * Create a new library card for a user
     * @param requestDTO the request containing user ID and expiry date
     * @return the created library card
     */
    LibraryCardResponseDTO createLibraryCard(CreateLibraryCardRequestDTO requestDTO);
    
    /**
     * Get a library card by its ID
     * @param id the library card ID
     * @return the library card
     */
    LibraryCardResponseDTO getLibraryCardById(Long id);
    
    /**
     * Get all library cards for a user
     * @param userId the user ID
     * @return list of library cards
     */
    List<LibraryCardResponseDTO> getLibraryCardsByUserId(Long userId);
    
    /**
     * Get all library cards with optional filtering by status
     * @param status the status to filter by (optional)
     * @return list of library cards
     */
    List<LibraryCardResponseDTO> getAllLibraryCards(LibraryCardStatus status);
    
    /**
     * Update the status of a library card
     * @param id the library card ID
     * @param requestDTO the request containing the new status
     * @return the updated library card
     */
    LibraryCardResponseDTO updateLibraryCardStatus(Long id, UpdateLibraryCardStatusRequestDTO requestDTO);
    
    /**
     * Renew a library card with a new expiry date
     * @param id the library card ID
     * @param requestDTO the request containing the new expiry date
     * @return the renewed library card
     */
    LibraryCardResponseDTO renewLibraryCard(Long id, RenewLibraryCardRequestDTO requestDTO);
}

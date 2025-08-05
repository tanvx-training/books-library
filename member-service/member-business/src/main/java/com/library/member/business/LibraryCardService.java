package com.library.member.business;

import com.library.member.business.dto.request.CreateLibraryCardRequest;
import com.library.member.business.dto.request.UpdateCardStatusRequest;
import com.library.member.business.dto.response.LibraryCardResponse;

import java.util.List;
import java.util.UUID;

public interface LibraryCardService {

    LibraryCardResponse createLibraryCard(CreateLibraryCardRequest request);

    LibraryCardResponse getLibraryCard(UUID cardId);

    LibraryCardResponse updateLibraryCardStatus(UUID cardId, UpdateCardStatusRequest request);

    List<LibraryCardResponse> getUserLibraryCards(String keycloakId);

    List<LibraryCardResponse> getCurrentUserLibraryCards();

    List<LibraryCardResponse> getUserActiveLibraryCards(String keycloakId);

    boolean hasActiveLibraryCard(String keycloakId);

    void deactivateLibraryCard(UUID cardId);
}
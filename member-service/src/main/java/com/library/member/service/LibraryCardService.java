package com.library.member.service;

import com.library.member.dto.request.CreateLibraryCardRequest;
import com.library.member.dto.request.UpdateCardStatusRequest;
import com.library.member.dto.response.LibraryCardResponse;

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
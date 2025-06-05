package com.library.user.domain.service;

import com.library.user.presentation.dto.request.CreateLibraryCardRequestDTO;
import com.library.user.presentation.dto.response.LibraryCardResponseDTO;
import jakarta.validation.Valid;

public interface LibraryCardService {

    LibraryCardResponseDTO createLibraryCard(CreateLibraryCardRequestDTO requestDTO);
}

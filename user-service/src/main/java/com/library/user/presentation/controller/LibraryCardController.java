package com.library.user.presentation.controller;

import com.library.user.domain.service.LibraryCardService;
import com.library.user.presentation.dto.request.CreateLibraryCardRequestDTO;
import com.library.user.presentation.dto.response.LibraryCardResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/library-cards")
public class LibraryCardController {

    private final LibraryCardService libraryCardService;

    @PostMapping
    public ResponseEntity<LibraryCardResponseDTO> createLibraryCard(@Valid @RequestBody CreateLibraryCardRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(libraryCardService.createLibraryCard(requestDTO));
    }
}

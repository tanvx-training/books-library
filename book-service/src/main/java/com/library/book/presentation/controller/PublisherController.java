package com.library.book.presentation.controller;

import com.library.book.domain.service.AuthorService;
import com.library.book.domain.service.PublisherService;
import com.library.book.presentation.dto.request.AuthorCreateDTO;
import com.library.book.presentation.dto.request.PublisherCreateDTO;
import com.library.book.presentation.dto.response.AuthorResponseDTO;
import com.library.book.presentation.dto.response.BookResponseDTO;
import com.library.book.presentation.dto.response.PublisherResponseDTO;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/publishers")
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<PageResponseDTO<PublisherResponseDTO>> getAllPublishers(
            @Valid @ModelAttribute PageRequestDTO pageRequestDTO) {
        return ResponseEntity.ok(publisherService.getAllPublishers(pageRequestDTO));
    }

    @GetMapping("/{publisherId}/books")
    public ResponseEntity<PageResponseDTO<BookResponseDTO>> getBooksByPublisher(
            @PathVariable("publisherId") Long publisherId,
            @Valid @ModelAttribute PageRequestDTO pageRequestDTO) {
        return ResponseEntity.ok(publisherService.getBooksByPublisher(publisherId, pageRequestDTO));
    }

    @PostMapping
    public ResponseEntity<PublisherResponseDTO> createPublisher(
            @RequestBody @Valid PublisherCreateDTO publisherCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(publisherService.createPublisher(publisherCreateDTO));
    }
}

package com.library.book.controller;

import com.library.book.service.PublisherService;
import com.library.book.dto.request.PublisherCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.book.dto.response.PublisherResponseDTO;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

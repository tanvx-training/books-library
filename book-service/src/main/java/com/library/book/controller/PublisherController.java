package com.library.book.controller;

import com.library.book.service.PublisherService;
import com.library.book.dto.request.PublisherCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.book.dto.response.PublisherResponseDTO;
import com.library.common.dto.ApiResponse;
import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/publishers")
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<PublisherResponseDTO>>> getAllPublishers(
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(publisherService.getAllPublishers(paginatedRequest)));
    }

    @GetMapping("/{publisherId}/books")
    public ResponseEntity<ApiResponse<PaginatedResponse<BookResponseDTO>>> getBooksByPublisher(
            @PathVariable("publisherId") Long publisherId,
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(publisherService.getBooksByPublisher(publisherId, paginatedRequest)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PublisherResponseDTO>> createPublisher(
            @RequestBody @Valid PublisherCreateDTO publisherCreateDTO) {
        return ResponseEntity.ok(ApiResponse.success(publisherService.createPublisher(publisherCreateDTO)));
    }
}

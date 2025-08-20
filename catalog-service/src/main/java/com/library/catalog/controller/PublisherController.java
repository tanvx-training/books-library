package com.library.catalog.controller;

import com.library.catalog.dto.request.CreatePublisherRequest;
import com.library.catalog.dto.request.PublisherSearchRequest;
import com.library.catalog.dto.request.UpdatePublisherRequest;
import com.library.catalog.dto.response.PagedPublisherResponse;
import com.library.catalog.dto.response.PublisherResponse;
import com.library.catalog.framework.annotation.ValidUuid;
import com.library.catalog.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("/api/v/publishers")
@Validated
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    @PostMapping
    public ResponseEntity<PublisherResponse> createPublisher(@Valid @RequestBody CreatePublisherRequest request) {

        PublisherResponse response = publisherService.createPublisher(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{public_id}")
    public ResponseEntity<PublisherResponse> getPublisher(@PathVariable("public_id") @ValidUuid String publicId) {

        UUID uuid = UUID.fromString(publicId);
        PublisherResponse response = publisherService.getPublisherByPublicId(uuid);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedPublisherResponse> getAllPublishers(@Valid @ModelAttribute PublisherSearchRequest request) {

        PagedPublisherResponse response = publisherService.getAllPublishers(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{public_id}")
    public ResponseEntity<PublisherResponse> updatePublisher(@PathVariable("public_id") @ValidUuid String publicId,
            @Valid @RequestBody UpdatePublisherRequest request) {

        UUID uuid = UUID.fromString(publicId);
        PublisherResponse response = publisherService.updatePublisher(uuid, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{public_id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable("public_id") @ValidUuid String publicId) {

        UUID uuid = UUID.fromString(publicId);
        publisherService.deletePublisher(uuid);
        return ResponseEntity.noContent().build();
    }
}
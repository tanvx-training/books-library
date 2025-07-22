package com.library.catalog.controller.rest;

import com.library.catalog.controller.util.UserContextUtil;
import com.library.catalog.business.PublisherBusiness;
import com.library.catalog.business.dto.request.CreatePublisherRequest;
import com.library.catalog.business.dto.request.UpdatePublisherRequest;
import com.library.catalog.business.dto.response.PublisherResponse;
import com.library.catalog.business.dto.response.PagedPublisherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/v1/publishers")
@Validated
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherBusiness publisherBusiness;

    @PostMapping
    public ResponseEntity<PublisherResponse> createPublisher(@Valid @RequestBody CreatePublisherRequest request) {
        String currentUser = UserContextUtil.getCurrentUser();
        PublisherResponse response = publisherBusiness.createPublisher(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherResponse> getPublisher(@PathVariable @Positive(message = "Publisher ID must be positive") Integer id) {
        PublisherResponse response = publisherBusiness.getPublisherById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedPublisherResponse> getAllPublishers(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be non-negative") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size must not exceed 100") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortBy);
        PagedPublisherResponse response = publisherBusiness.getAllPublishers(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedPublisherResponse> searchPublishers(
            @RequestParam @NotBlank(message = "Search name cannot be blank") String name,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be non-negative") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size must not exceed 100") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortBy);
        PagedPublisherResponse response = publisherBusiness.searchPublishersByName(name, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublisherResponse> updatePublisher(
            @PathVariable @Positive(message = "Publisher ID must be positive") Integer id,
            @Valid @RequestBody UpdatePublisherRequest request) {
        
        String currentUser = UserContextUtil.getCurrentUser();
        PublisherResponse response = publisherBusiness.updatePublisher(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable @Positive(message = "Publisher ID must be positive") Integer id) {
        String currentUser = UserContextUtil.getCurrentUser();
        publisherBusiness.deletePublisher(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
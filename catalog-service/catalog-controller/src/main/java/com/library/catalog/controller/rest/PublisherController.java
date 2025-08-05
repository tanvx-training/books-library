package com.library.catalog.controller.rest;

import com.library.catalog.business.dto.request.PublisherSearchRequest;
import com.library.catalog.controller.util.UserContextUtil;
import com.library.catalog.business.PublisherBusiness;
import com.library.catalog.business.dto.request.CreatePublisherRequest;
import com.library.catalog.business.dto.request.UpdatePublisherRequest;
import com.library.catalog.business.dto.response.PublisherResponse;
import com.library.catalog.business.dto.response.PagedPublisherResponse;
import com.library.catalog.business.validation.ValidUuid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.UUID;

@RestController
@RequestMapping("/api/v/publishers")
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

    @GetMapping("/{public_id}")
    public ResponseEntity<PublisherResponse> getPublisher(@PathVariable("public_id") @ValidUuid String publicId) {

        UUID uuid = UUID.fromString(publicId);
        PublisherResponse response = publisherBusiness.getPublisherByPublicId(uuid);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedPublisherResponse> getAllPublishers(@Valid @ModelAttribute PublisherSearchRequest request) {

        PagedPublisherResponse response = publisherBusiness.getAllPublishers(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{public_id}")
    public ResponseEntity<PublisherResponse> updatePublisher(@PathVariable("public_id") @ValidUuid String publicId,
            @Valid @RequestBody UpdatePublisherRequest request) {

        UUID uuid = UUID.fromString(publicId);
        String currentUser = UserContextUtil.getCurrentUser();
        PublisherResponse response = publisherBusiness.updatePublisher(uuid, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{public_id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable("public_id") @ValidUuid String publicId) {

        UUID uuid = UUID.fromString(publicId);
        String currentUser = UserContextUtil.getCurrentUser();
        publisherBusiness.deletePublisher(uuid, currentUser);
        return ResponseEntity.noContent().build();
    }
}
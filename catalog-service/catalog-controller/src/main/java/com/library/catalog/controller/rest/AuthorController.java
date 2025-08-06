package com.library.catalog.controller.rest;

import com.library.catalog.business.dto.request.AuthorSearchRequest;
import com.library.catalog.business.AuthorBusiness;
import com.library.catalog.business.dto.request.CreateAuthorRequest;
import com.library.catalog.business.dto.request.UpdateAuthorRequest;
import com.library.catalog.business.dto.response.AuthorResponse;
import com.library.catalog.business.dto.response.PagedAuthorResponse;
import com.library.catalog.business.validation.ValidUuid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authors")
@Validated
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorBusiness authorBusiness;

    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody CreateAuthorRequest request) {

        return new ResponseEntity<>(authorBusiness.createAuthor(request), HttpStatus.CREATED);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<AuthorResponse> getAuthor(
            @PathVariable @ValidUuid(allowNull = false, message = "Public ID must be a valid UUID") String publicId) {

        return ResponseEntity.ok(authorBusiness.getAuthorByPublicId(UUID.fromString(publicId)));
    }

    @GetMapping
    public ResponseEntity<PagedAuthorResponse> getAllAuthors(@Valid @ModelAttribute AuthorSearchRequest request) {

        PagedAuthorResponse response = authorBusiness.getAllAuthors(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @PathVariable @ValidUuid(allowNull = false, message = "Public ID must be a valid UUID") String publicId,
            @Valid @RequestBody UpdateAuthorRequest request) {

        UUID uuid = UUID.fromString(publicId);
        AuthorResponse response = authorBusiness.updateAuthor(uuid, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteAuthor(
            @PathVariable @ValidUuid(allowNull = false, message = "Public ID must be a valid UUID") String publicId) {

        UUID uuid = UUID.fromString(publicId);
        authorBusiness.deleteAuthor(uuid);
        return ResponseEntity.noContent().build();
    }
}
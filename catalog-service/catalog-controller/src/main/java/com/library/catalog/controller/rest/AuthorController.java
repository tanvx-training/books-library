package com.library.catalog.controller.rest;

import com.library.catalog.business.dto.request.AuthorSearchRequest;
import com.library.catalog.controller.util.UserContextUtil;
import com.library.catalog.business.AuthorBusiness;
import com.library.catalog.business.dto.request.CreateAuthorRequest;
import com.library.catalog.business.dto.request.UpdateAuthorRequest;
import com.library.catalog.business.dto.response.AuthorResponse;
import com.library.catalog.business.dto.response.PagedAuthorResponse;
import com.library.catalog.business.aop.exception.InvalidUuidException;
import com.library.catalog.business.validation.ValidUuid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authors")
@Validated
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorBusiness authorBusiness;

    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody CreateAuthorRequest request) {

        String currentUser = UserContextUtil.getCurrentUser();
        return new ResponseEntity<>(authorBusiness.createAuthor(request, currentUser), HttpStatus.CREATED);
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
        String currentUser = UserContextUtil.getCurrentUser();
        AuthorResponse response = authorBusiness.updateAuthor(uuid, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteAuthor(
            @PathVariable @ValidUuid(allowNull = false, message = "Public ID must be a valid UUID") String publicId) {

        UUID uuid = UUID.fromString(publicId);
        String currentUser = UserContextUtil.getCurrentUser();
        authorBusiness.deleteAuthor(uuid, currentUser);
        return ResponseEntity.noContent().build();
    }
}
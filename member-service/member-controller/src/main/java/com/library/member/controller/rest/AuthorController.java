package com.library.catalog.controller.rest;

import com.library.catalog.controller.util.UserContextUtil;
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
@RequestMapping("/api/v1/authors") // Different path to avoid conflicts
@Validated
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorBusiness authorBusiness;

    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody CreateAuthorRequest request) {
        String currentUser = UserContextUtil.getCurrentUser();
        AuthorResponse response = authorBusiness.createAuthor(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthor(@PathVariable @Positive(message = "Author ID must be positive") Integer id) {
        AuthorResponse response = authorBusiness.getAuthorById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedAuthorResponse> getAllAuthors(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be non-negative") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size must not exceed 100") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortBy);
        PagedAuthorResponse response = authorBusiness.getAllAuthors(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedAuthorResponse> searchAuthors(
            @RequestParam @NotBlank(message = "Search name cannot be blank") String name,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be non-negative") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size must not exceed 100") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortBy);
        String currentUser = UserContextUtil.getCurrentUser();
        PagedAuthorResponse response = authorBusiness.searchAuthorsByName(name, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @PathVariable @Positive(message = "Author ID must be positive") Integer id,
            @Valid @RequestBody UpdateAuthorRequest request) {
        
        String currentUser = UserContextUtil.getCurrentUser();
        AuthorResponse response = authorBusiness.updateAuthor(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable @Positive(message = "Author ID must be positive") Integer id) {
        String currentUser = UserContextUtil.getCurrentUser();
        authorBusiness.deleteAuthor(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
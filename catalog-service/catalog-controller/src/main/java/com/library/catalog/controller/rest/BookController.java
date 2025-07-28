package com.library.catalog.controller.rest;

import com.library.catalog.controller.util.UserContextUtil;
import com.library.catalog.business.BookBusiness;
import com.library.catalog.business.dto.request.BookSearchRequest;
import com.library.catalog.business.dto.request.CreateBookRequest;
import com.library.catalog.business.dto.request.UpdateBookRequest;
import com.library.catalog.business.dto.response.BookDetailResponse;
import com.library.catalog.business.dto.response.BookResponse;
import com.library.catalog.business.dto.response.PagedBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@Validated
@RequiredArgsConstructor
public class BookController {

    private final BookBusiness bookBusiness;

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookRequest request) {
        String currentUser = UserContextUtil.getCurrentUser();
        BookResponse response = bookBusiness.createBook(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{publicId}/detail")
    public ResponseEntity<BookDetailResponse> getBookDetail(@PathVariable String publicId) {

        UUID uuid = UUID.fromString(publicId);
        BookDetailResponse response = bookBusiness.getBookDetail(uuid);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedBookResponse> getAllBooks(@Valid @ModelAttribute BookSearchRequest request) {

        PagedBookResponse response = bookBusiness.searchBooks(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable String publicId, @Valid @RequestBody UpdateBookRequest request) {

        UUID uuid = UUID.fromString(publicId);
        String currentUser = UserContextUtil.getCurrentUser();
        BookResponse response = bookBusiness.updateBook(uuid, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteBook(@PathVariable String publicId) {

        UUID uuid = UUID.fromString(publicId);
        String currentUser = UserContextUtil.getCurrentUser();
        bookBusiness.deleteBook(uuid, currentUser);
        return ResponseEntity.noContent().build();
    }
}
package com.library.catalog.controller;

import com.library.catalog.dto.request.BookSearchRequest;
import com.library.catalog.dto.request.CreateBookWithCopiesRequest;
import com.library.catalog.dto.request.UpdateBookWithCopiesRequest;
import com.library.catalog.dto.response.BookDetailResponse;
import com.library.catalog.dto.response.PagedBookResponse;
import com.library.catalog.service.BookService;
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

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookDetailResponse> createBookWithCopies(@Valid @RequestBody CreateBookWithCopiesRequest request) {

        BookDetailResponse response = bookService.createBookWithCopies(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{publicId}/detail")
    public ResponseEntity<BookDetailResponse> getBookDetail(@PathVariable String publicId) {

        UUID uuid = UUID.fromString(publicId);
        BookDetailResponse response = bookService.getBookDetail(uuid);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedBookResponse> getAllBooks(@Valid @ModelAttribute BookSearchRequest request) {

        PagedBookResponse response = bookService.searchBooks(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<BookDetailResponse> updateBookWithCopies(@PathVariable String publicId, @Valid @RequestBody UpdateBookWithCopiesRequest request) {

        UUID uuid = UUID.fromString(publicId);
        BookDetailResponse response = bookService.updateBookWithCopies(uuid, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteBook(@PathVariable String publicId) {

        UUID uuid = UUID.fromString(publicId);
        bookService.deleteBook(uuid);
        return ResponseEntity.noContent().build();
    }
}
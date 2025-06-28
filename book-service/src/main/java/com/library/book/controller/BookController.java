package com.library.book.controller;

import com.library.book.service.BookService;
import com.library.book.dto.request.BookCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<PageResponseDTO<BookResponseDTO>> getAllBooks(@Valid @ModelAttribute PageRequestDTO pageRequestDTO) {
        return ResponseEntity.ok(bookService.getAllBooks(pageRequestDTO));
    }

    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookCreateDTO bookCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.createBook(bookCreateDTO));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(bookService.getBookById(bookId));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDTO<BookResponseDTO>> searchBooks(@RequestParam(value = "keyword") String keyword,
                                                                        @Valid @ModelAttribute PageRequestDTO pageRequestDTO) {
        return ResponseEntity.ok(bookService.searchBooks(keyword, pageRequestDTO));
    }
}

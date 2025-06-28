package com.library.book.controller;

import com.library.book.service.BookService;
import com.library.book.dto.request.BookCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.common.dto.ApiResponse;
import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<BookResponseDTO>>> getAllBooks(
            @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(bookService.getAllBooks(paginatedRequest)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookResponseDTO>> createBook(@Valid @RequestBody BookCreateDTO bookCreateDTO) {
        return ResponseEntity.ok(ApiResponse.success(bookService.createBook(bookCreateDTO)));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<ApiResponse<BookResponseDTO>> getBookById(@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(ApiResponse.success(bookService.getBookById(bookId)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaginatedResponse<BookResponseDTO>>> searchBooks(@RequestParam(value = "keyword") String keyword,
                                                                        @Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(bookService.searchBooks(keyword, paginatedRequest)));
    }
}

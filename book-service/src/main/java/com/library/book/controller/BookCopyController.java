package com.library.book.controller;

import com.library.book.service.BookCopyService;
import com.library.book.dto.request.BookCopyRequestDTO;
import com.library.book.dto.request.BookCopyUpdateDTO;
import com.library.book.dto.response.BookCopyResponseDTO;
import com.library.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/copies")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    @GetMapping("/{bookId}/books")
    public ResponseEntity<ApiResponse<List<BookCopyResponseDTO>>> getBookCopiesByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(ApiResponse.success(bookCopyService.getBookCopiesByBookId(bookId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookCopyResponseDTO>> addBookCopy(
            @Valid @RequestBody BookCopyRequestDTO bookCopyRequestDTO) {
        return ResponseEntity.ok(ApiResponse.success(bookCopyService.addBookCopy(bookCopyRequestDTO)));
    }

    @GetMapping("/{bookCopyId}")
    public ResponseEntity<ApiResponse<BookCopyResponseDTO>> getBookCopyById(@PathVariable Long bookCopyId) {
        return ResponseEntity.ok(ApiResponse.success(bookCopyService.getBookCopyById(bookCopyId)));
    }

    @PutMapping("/{bookCopyId}")
    public ResponseEntity<ApiResponse<BookCopyResponseDTO>> updateBookCopy(
            @PathVariable Long bookCopyId,
            @Valid @RequestBody BookCopyUpdateDTO bookCopyRequestDTO) {
        return ResponseEntity.ok(ApiResponse.success(bookCopyService.updateBookCopy(bookCopyId, bookCopyRequestDTO)));
    }

    @PatchMapping("/{bookCopyId}/status")
    public ResponseEntity<ApiResponse<BookCopyResponseDTO>> updateBookCopyStatus(
            @PathVariable Long bookCopyId,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success(bookCopyService.updateBookCopyStatus(bookCopyId, status)));
    }

    @DeleteMapping("/{bookCopyId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteBookCopy(@PathVariable Long bookCopyId) {
        boolean deleted = bookCopyService.deleteBookCopy(bookCopyId);
        return ResponseEntity.ok(ApiResponse.success(deleted));
    }
} 
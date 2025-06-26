package com.library.book.presentation.controller;

import com.library.book.domain.service.BookCopyService;
import com.library.book.presentation.dto.request.BookCopyRequestDTO;
import com.library.book.presentation.dto.request.BookCopyUpdateDTO;
import com.library.book.presentation.dto.response.BookCopyResponseDTO;
import com.library.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/copies")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    @GetMapping("/{bookId}/books")
    public ResponseEntity<List<BookCopyResponseDTO>> getBookCopiesByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookCopyService.getBookCopiesByBookId(bookId));
    }

    @PostMapping
    public ResponseEntity<BookCopyResponseDTO> addBookCopy(
            @Valid @RequestBody BookCopyRequestDTO bookCopyRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookCopyService.addBookCopy(bookCopyRequestDTO));
    }

    @GetMapping("/{bookCopyId}")
    public ResponseEntity<BookCopyResponseDTO> getBookCopyById(@PathVariable Long bookCopyId) {
        return ResponseEntity.ok(bookCopyService.getBookCopyById(bookCopyId));
    }

    @PutMapping("/{bookCopyId}")
    public ResponseEntity<BookCopyResponseDTO> updateBookCopy(
            @PathVariable Long bookCopyId,
            @Valid @RequestBody BookCopyUpdateDTO bookCopyRequestDTO) {
        return ResponseEntity.ok(bookCopyService.updateBookCopy(bookCopyId, bookCopyRequestDTO));
    }

    @PatchMapping("/{bookCopyId}/status")
    public ResponseEntity<BookCopyResponseDTO> updateBookCopyStatus(
            @PathVariable Long bookCopyId,
            @RequestParam String status) {
        return ResponseEntity.ok(bookCopyService.updateBookCopyStatus(bookCopyId, status));
    }

    @DeleteMapping("/{bookCopyId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteBookCopy(@PathVariable Long bookCopyId) {
        boolean deleted = bookCopyService.deleteBookCopy(bookCopyId);
        return ResponseEntity.ok(ApiResponse.success(deleted, "Bản sao sách đã được xóa thành công"));
    }
} 
package com.library.book.presentation.controller;

import com.library.book.domain.service.AuthorService;
import com.library.book.presentation.dto.request.AuthorCreateDTO;
import com.library.book.presentation.dto.response.AuthorResponseDTO;
import com.library.book.presentation.dto.response.BookResponseDTO;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<PageResponseDTO<AuthorResponseDTO>> getAllCategories(
            @Valid @ModelAttribute PageRequestDTO pageRequestDTO) {
        return ResponseEntity.ok(authorService.getAllAuthors(pageRequestDTO));
    }

    @GetMapping("/{authorId}/books")
    public ResponseEntity<List<BookResponseDTO>> getBooksByAuthor(
            @PathVariable("authorId") Long authorId) {
        return ResponseEntity.ok(authorService.getBooksByAuthor(authorId));
    }

    @PostMapping
    public ResponseEntity<AuthorResponseDTO> createAuthor(
            @RequestBody @Valid AuthorCreateDTO authorCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authorService.createAuthor(authorCreateDTO));
    }
}

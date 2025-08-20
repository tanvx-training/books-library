package com.library.loan.controller;

import com.library.loan.service.BookBagService;
import com.library.loan.dto.request.AddBookToBagRequest;
import com.library.loan.dto.request.UpdateBookQuantityRequest;
import com.library.loan.dto.response.BookBagResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/book-bags")
@RequiredArgsConstructor
public class BookBagController {

    private final BookBagService bookBagService;

    @GetMapping("/{user_public_id}")
    public ResponseEntity<BookBagResponse> getUserBookBag(@PathVariable("user_public_id") String userPublicId) {

        return ResponseEntity.ok(bookBagService.getUserBookBag(userPublicId));
    }

    @PostMapping("/{user_public_id}/items")
    public ResponseEntity<BookBagResponse> addBookToBag(@PathVariable("user_public_id") String userPublicId,
            @Valid @RequestBody AddBookToBagRequest request) {

        return ResponseEntity.ok(bookBagService.addBookToBag(userPublicId, request));
    }

    @PutMapping("/{user_public_id}/items/{book_public_id}")
    public ResponseEntity<BookBagResponse> updateBookQuantity(@PathVariable("user_public_id") String userPublicId,
            @PathVariable("book_public_id") String bookPublicId, @Valid @RequestBody UpdateBookQuantityRequest request) {

        return ResponseEntity.ok(bookBagService.updateBookQuantity(userPublicId, bookPublicId, request));
    }

    @DeleteMapping("/{user_public_id}/items/{book_public_id}")
    public ResponseEntity<Void> removeBookFromBag(@PathVariable("user_public_id") String userPublicId,
            @PathVariable("book_public_id") String bookPublicId) {

        bookBagService.removeBookFromBag(userPublicId, bookPublicId);
        return ResponseEntity.noContent().build();
    }

    // Additional convenience endpoints

    @DeleteMapping("/{user_public_id}/clear")
    public ResponseEntity<Void> clearBookBag(@PathVariable("user_public_id") String userPublicId) {
        bookBagService.clearBookBag(userPublicId);
        return ResponseEntity.noContent().build();
    }
}
package com.library.loan.controller.rest;

import com.library.loan.business.BookBagBusiness;
import com.library.loan.business.dto.request.AddBookToBagRequest;
import com.library.loan.business.dto.request.UpdateBookQuantityRequest;
import com.library.loan.business.dto.response.BookBagResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/book-bags")
@RequiredArgsConstructor
public class BookBagController {

    private final BookBagBusiness bookBagBusiness;

    @GetMapping("/{user_public_id}")
    public ResponseEntity<BookBagResponse> getUserBookBag(@PathVariable("user_public_id") String userPublicId) {

        return ResponseEntity.ok(bookBagBusiness.getUserBookBag(userPublicId));
    }

    @PostMapping("/{user_public_id}/items")
    public ResponseEntity<BookBagResponse> addBookToBag(@PathVariable("user_public_id") String userPublicId,
            @Valid @RequestBody AddBookToBagRequest request) {

        return ResponseEntity.ok(bookBagBusiness.addBookToBag(userPublicId, request));
    }

    @PutMapping("/{user_public_id}/items/{book_public_id}")
    public ResponseEntity<BookBagResponse> updateBookQuantity(@PathVariable("user_public_id") String userPublicId,
            @PathVariable("book_public_id") String bookPublicId, @Valid @RequestBody UpdateBookQuantityRequest request) {

        return ResponseEntity.ok(bookBagBusiness.updateBookQuantity(userPublicId, bookPublicId, request));
    }

    @DeleteMapping("/{user_public_id}/items/{book_public_id}")
    public ResponseEntity<Void> removeBookFromBag(@PathVariable("user_public_id") String userPublicId,
            @PathVariable("book_public_id") String bookPublicId) {

        bookBagBusiness.removeBookFromBag(userPublicId, bookPublicId);
        return ResponseEntity.noContent().build();
    }

    // Additional convenience endpoints

    @DeleteMapping("/{user_public_id}/clear")
    public ResponseEntity<Void> clearBookBag(@PathVariable("user_public_id") String userPublicId) {
        bookBagBusiness.clearBookBag(userPublicId);
        return ResponseEntity.noContent().build();
    }
}
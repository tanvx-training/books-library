package com.library.loan.controller;

import com.library.loan.service.BorrowingService;
import com.library.loan.dto.request.BorrowingSearchRequest;
import com.library.loan.dto.request.CreateBorrowingRequest;
import com.library.loan.dto.request.RenewBorrowingRequest;
import com.library.loan.dto.request.ReturnBookRequest;
import com.library.loan.dto.response.BorrowingResponse;
import com.library.loan.dto.response.PagedBorrowingResponse;
import com.library.loan.aop.InvalidUuidException;
import com.library.loan.service.UnifiedAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/borrowings")
@RequiredArgsConstructor
@Validated
public class BorrowingController {

    private final BorrowingService borrowingService;
    private final UnifiedAuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<PagedBorrowingResponse> getAllBorrowings(@Valid @ModelAttribute BorrowingSearchRequest request) {
        
       return ResponseEntity.ok(borrowingService.getAllBorrowings(request));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<BorrowingResponse> getBorrowingByPublicId(
            @PathVariable String publicId) {
        
        UUID borrowingPublicId = parseUuid(publicId);
        BorrowingResponse response = borrowingService.getBorrowingByPublicId(borrowingPublicId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BorrowingResponse> createBorrowing(
            @Valid @RequestBody CreateBorrowingRequest request) {

        BorrowingResponse response = borrowingService.createBorrowing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{publicId}/return")
    public ResponseEntity<BorrowingResponse> returnBook(@PathVariable String publicId,
            @Valid @RequestBody ReturnBookRequest request) {
        
        UUID borrowingPublicId = parseUuid(publicId);
        BorrowingResponse response = borrowingService.returnBook(borrowingPublicId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{publicId}/renew")
    public ResponseEntity<BorrowingResponse> renewBorrowing(@PathVariable String publicId,
            @Valid @RequestBody RenewBorrowingRequest request) {
        
        UUID borrowingPublicId = parseUuid(publicId);
        BorrowingResponse response = borrowingService.renewBorrowing(borrowingPublicId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteBorrowing(@PathVariable String publicId) {
        
        UUID borrowingPublicId = parseUuid(publicId);
        borrowingService.deleteBorrowing(borrowingPublicId);
        return ResponseEntity.noContent().build();
    }

    public boolean canAccessBorrowing(String publicId) {
        try {
            UUID borrowingPublicId = parseUuid(publicId);
            
            // Get current user
            String currentUserId = authenticationService.getCurrentUserKeycloakId();
            if (currentUserId == null) {
                return false;
            }
            
            // Get the borrowing record to check ownership
            BorrowingResponse borrowing = borrowingService.getBorrowingByPublicId(borrowingPublicId);
            
            // Check if the current user is the owner of the borrowing
            return currentUserId.equals(borrowing.getUserPublicId().toString());
            
        } catch (Exception e) {
            log.warn("Error checking borrowing access for publicId: {}, error: {}", publicId, e.getMessage());
            return false;
        }
    }

    public boolean canCreateBorrowingForUser(UUID userPublicId) {
        try {
            // Get current user
            String currentUserId = authenticationService.getCurrentUserKeycloakId();
            if (currentUserId == null) {
                return false;
            }
            
            // Check if the current user is creating a borrowing for themselves
            return currentUserId.equals(userPublicId.toString());
            
        } catch (Exception e) {
            log.warn("Error checking borrowing creation access for userPublicId: {}, error: {}", userPublicId, e.getMessage());
            return false;
        }
    }

    private UUID parseUuid(String uuidString) {
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("publicId", uuidString);
        }
    }
}
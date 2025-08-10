package com.library.loan.controller.rest;

import com.library.loan.business.BorrowingBusiness;
import com.library.loan.business.dto.request.BorrowingSearchRequest;
import com.library.loan.business.dto.request.CreateBorrowingRequest;
import com.library.loan.business.dto.request.RenewBorrowingRequest;
import com.library.loan.business.dto.request.ReturnBookRequest;
import com.library.loan.business.dto.response.BorrowingResponse;
import com.library.loan.business.dto.response.PagedBorrowingResponse;
import com.library.loan.business.exception.InvalidUuidException;
import com.library.loan.business.security.UnifiedAuthenticationService;
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

    private final BorrowingBusiness borrowingBusiness;
    private final UnifiedAuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<PagedBorrowingResponse> getAllBorrowings(@Valid @ModelAttribute BorrowingSearchRequest request) {
        
       return ResponseEntity.ok(borrowingBusiness.getAllBorrowings(request));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<BorrowingResponse> getBorrowingByPublicId(
            @PathVariable String publicId) {
        
        UUID borrowingPublicId = parseUuid(publicId);
        BorrowingResponse response = borrowingBusiness.getBorrowingByPublicId(borrowingPublicId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BorrowingResponse> createBorrowing(
            @Valid @RequestBody CreateBorrowingRequest request) {

        BorrowingResponse response = borrowingBusiness.createBorrowing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{publicId}/return")
    public ResponseEntity<BorrowingResponse> returnBook(@PathVariable String publicId,
            @Valid @RequestBody ReturnBookRequest request) {
        
        UUID borrowingPublicId = parseUuid(publicId);
        BorrowingResponse response = borrowingBusiness.returnBook(borrowingPublicId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{publicId}/renew")
    public ResponseEntity<BorrowingResponse> renewBorrowing(@PathVariable String publicId,
            @Valid @RequestBody RenewBorrowingRequest request) {
        
        UUID borrowingPublicId = parseUuid(publicId);
        BorrowingResponse response = borrowingBusiness.renewBorrowing(borrowingPublicId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteBorrowing(@PathVariable String publicId) {
        
        UUID borrowingPublicId = parseUuid(publicId);
        borrowingBusiness.deleteBorrowing(borrowingPublicId);
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
            BorrowingResponse borrowing = borrowingBusiness.getBorrowingByPublicId(borrowingPublicId);
            
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
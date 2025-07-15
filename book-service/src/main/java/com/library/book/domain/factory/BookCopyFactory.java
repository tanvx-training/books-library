package com.library.book.domain.factory;

import com.library.book.domain.model.book.BookId;
import com.library.book.domain.model.bookcopy.*;
import com.library.book.domain.repository.BookCopyRepository;
import com.library.book.domain.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Factory for creating BookCopy aggregates with proper validation
 */
@Component
@RequiredArgsConstructor
public class BookCopyFactory {
    
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    
    /**
     * Create a new book copy with validation
     */
    public BookCopy createBookCopy(BookCopyCreationRequest request) {
        validateBookCopyCreationRequest(request);
        
        BookId bookId = new BookId(request.getBookId());
        
        // Validate book exists
        if (!bookRepository.findById(bookId).isPresent()) {
            throw new IllegalArgumentException("Book with ID " + request.getBookId() + " does not exist");
        }
        
        // Validate copy number is unique for this book
        if (bookCopyRepository.existsByBookIdAndCopyNumber(bookId, request.getCopyNumber())) {
            throw new IllegalArgumentException("Copy number " + request.getCopyNumber() + 
                " already exists for book ID " + request.getBookId());
        }
        
        CopyNumber copyNumber = CopyNumber.of(request.getCopyNumber());
        BookCondition condition = request.getCondition() != null 
            ? request.getCondition() 
            : BookCondition.GOOD;
        Location location = StringUtils.hasText(request.getLocation())
            ? Location.of(request.getLocation())
            : Location.empty();
        
        return BookCopy.create(bookId, copyNumber, condition, location);
    }
    
    /**
     * Create multiple copies for a book
     */
    public java.util.List<BookCopy> createMultipleCopies(
            Long bookId, 
            int numberOfCopies, 
            BookCondition condition, 
            String locationPrefix) {
        
        if (numberOfCopies <= 0) {
            throw new IllegalArgumentException("Number of copies must be positive");
        }
        
        if (numberOfCopies > 100) {
            throw new IllegalArgumentException("Cannot create more than 100 copies at once");
        }
        
        BookId bookIdObj = new BookId(bookId);
        
        // Validate book exists
        if (!bookRepository.findById(bookIdObj).isPresent()) {
            throw new IllegalArgumentException("Book with ID " + bookId + " does not exist");
        }
        
        java.util.List<BookCopy> copies = new java.util.ArrayList<>();
        
        for (int i = 1; i <= numberOfCopies; i++) {
            String copyNumber = String.format("%03d", i);
            String location = StringUtils.hasText(locationPrefix) 
                ? locationPrefix + "-" + copyNumber
                : "SHELF-" + copyNumber;
            
            // Check if copy number already exists
            if (!bookCopyRepository.existsByBookIdAndCopyNumber(bookIdObj, copyNumber)) {
                BookCopy copy = BookCopy.create(
                    bookIdObj,
                    CopyNumber.of(copyNumber),
                    condition != null ? condition : BookCondition.GOOD,
                    Location.of(location)
                );
                copies.add(copy);
            }
        }
        
        return copies;
    }
    
    private void validateBookCopyCreationRequest(BookCopyCreationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Book copy creation request cannot be null");
        }
        
        if (request.getBookId() == null) {
            throw new IllegalArgumentException("Book ID is required");
        }
        
        if (!StringUtils.hasText(request.getCopyNumber())) {
            throw new IllegalArgumentException("Copy number is required");
        }
    }
    
    /**
     * Request object for book copy creation
     */
    @lombok.Data
    @lombok.Builder
    public static class BookCopyCreationRequest {
        private Long bookId;
        private String copyNumber;
        private BookCondition condition;
        private String location;
    }
}
package com.library.book.domain.factory;

import com.library.book.domain.exception.BookCopyDomainException;
import com.library.book.domain.exception.InvalidBookCopyDataException;
import com.library.book.domain.model.book.BookId;
import com.library.book.domain.model.bookcopy.BookCondition;
import com.library.book.domain.model.bookcopy.BookCopy;
import com.library.book.domain.model.bookcopy.CopyNumber;
import com.library.book.domain.model.bookcopy.Location;
import com.library.book.domain.repository.BookCopyRepository;
import com.library.book.domain.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

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
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookCopyDomainException("Book with ID " + request.getBookId() + " does not exist");
        }
        
        // Validate copy number is unique for this book
        if (bookCopyRepository.existsByBookIdAndCopyNumber(bookId, request.getCopyNumber())) {
            throw new BookCopyDomainException("Copy number " + request.getCopyNumber() +
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
    public List<BookCopy> createMultipleCopies(
            Long bookId, 
            int numberOfCopies, 
            BookCondition condition, 
            String locationPrefix) {
        
        if (numberOfCopies <= 0) {
            throw new BookCopyDomainException("Number of copies must be positive");
        }
        
        if (numberOfCopies > 100) {
            throw new BookCopyDomainException("Cannot create more than 100 copies at once");
        }
        
        BookId bookIdObj = new BookId(bookId);
        
        // Validate book exists
        if (bookRepository.findById(bookIdObj).isEmpty()) {
            throw new BookCopyDomainException("Book with ID " + bookId + " does not exist");
        }
        
        List<BookCopy> copies = new java.util.ArrayList<>();
        
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
            throw new BookCopyDomainException("Book copy creation request cannot be null");
        }
        
        if (request.getBookId() == null) {
            throw new BookCopyDomainException("Book ID is required");
        }
        
        if (!StringUtils.hasText(request.getCopyNumber())) {
            throw new BookCopyDomainException("Copy number is required");
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
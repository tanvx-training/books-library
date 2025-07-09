package com.library.book.domain.service;

import com.library.book.domain.exception.BookCopyNotFoundException;
import com.library.book.domain.exception.InvalidBookCopyDataException;
import com.library.book.domain.model.bookcopy.*;
import com.library.book.domain.repository.BookCopyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookCopyDomainService {
    
    private final BookCopyRepository bookCopyRepository;
    
    /**
     * Find all book copies for a specific book
     * 
     * @param bookId the book ID
     * @return list of book copies
     */
    public List<BookCopy> findBookCopiesByBookId(Long bookId) {
        if (bookId == null) {
            throw new InvalidBookCopyDataException("bookId", "Book ID cannot be null");
        }
        return bookCopyRepository.findByBookId(bookId);
    }
    
    /**
     * Create a new book copy
     * 
     * @param bookId the book ID
     * @param title the book title
     * @param copyNumber the copy number
     * @param status the status
     * @param condition the condition
     * @param location the location
     * @return the created book copy
     */
    public BookCopy createBookCopy(
            Long bookId,
            String title,
            String copyNumber,
            String status,
            String condition,
            String location) {
        
        BookReference bookReference = BookReference.of(bookId, title);
        CopyNumber copyNumberVO = CopyNumber.of(copyNumber);
        
        // Check if copy number already exists for this book
        if (bookCopyRepository.existsByBookIdAndCopyNumber(bookId, copyNumberVO)) {
            throw new InvalidBookCopyDataException("copyNumber", 
                "Copy number already exists for this book: " + copyNumber);
        }
        
        BookCopyStatus bookCopyStatus = status != null && !status.isEmpty() 
            ? BookCopyStatus.fromString(status) 
            : BookCopyStatus.AVAILABLE;
            
        BookCopyCondition bookCopyCondition = condition != null && !condition.isEmpty()
            ? BookCopyCondition.fromString(condition)
            : null;
            
        Location locationVO = location != null && !location.isEmpty()
            ? Location.of(location)
            : Location.empty();
            
        BookCopy bookCopy = BookCopy.create(
            bookReference,
            copyNumberVO,
            bookCopyStatus,
            bookCopyCondition,
            locationVO
        );
        
        return bookCopyRepository.save(bookCopy);
    }
    
    /**
     * Update a book copy's status
     * 
     * @param bookCopyId the book copy ID
     * @param newStatus the new status
     * @return the updated book copy
     */
    public BookCopy updateBookCopyStatus(Long bookCopyId, String newStatus) {
        BookCopy bookCopy = bookCopyRepository.findById(BookCopyId.of(bookCopyId))
            .orElseThrow(() -> new BookCopyNotFoundException(bookCopyId));
            
        BookCopyStatus status = BookCopyStatus.fromString(newStatus);
        bookCopy.updateStatus(status);
        
        return bookCopyRepository.save(bookCopy);
    }
    
    /**
     * Update a book copy's details
     * 
     * @param bookCopyId the book copy ID
     * @param copyNumber the new copy number
     * @param condition the new condition
     * @param location the new location
     * @param status the new status
     * @return the updated book copy
     */
    public BookCopy updateBookCopy(
            Long bookCopyId,
            String copyNumber,
            String condition,
            String location,
            String status) {
        
        BookCopy bookCopy = bookCopyRepository.findById(BookCopyId.of(bookCopyId))
            .orElseThrow(() -> new BookCopyNotFoundException(bookCopyId));
            
        // Update copy number if provided
        if (copyNumber != null && !copyNumber.equals(bookCopy.getCopyNumber().getValue())) {
            CopyNumber newCopyNumber = CopyNumber.of(copyNumber);
            
            // Check if the new copy number conflicts with an existing one
            if (bookCopyRepository.existsByBookIdAndCopyNumber(
                    bookCopy.getBookReference().getBookId(), newCopyNumber)) {
                throw new InvalidBookCopyDataException("copyNumber", 
                    "Copy number already exists for this book: " + copyNumber);
            }
            
            bookCopy.updateCopyNumber(newCopyNumber);
        }
        
        // Update condition if provided
        if (condition != null) {
            BookCopyCondition newCondition = BookCopyCondition.fromString(condition);
            bookCopy.updateCondition(newCondition);
        }
        
        // Update location if provided
        if (location != null) {
            Location newLocation = Location.of(location);
            bookCopy.updateLocation(newLocation);
        }
        
        // Update status if provided
        if (status != null && !status.equals(bookCopy.getStatus().name())) {
            BookCopyStatus newStatus = BookCopyStatus.fromString(status);
            bookCopy.updateStatus(newStatus);
        }
        
        return bookCopyRepository.save(bookCopy);
    }
    
    /**
     * Delete a book copy
     * 
     * @param bookCopyId the book copy ID
     * @return true if deleted successfully
     */
    public boolean deleteBookCopy(Long bookCopyId) {
        BookCopy bookCopy = bookCopyRepository.findById(BookCopyId.of(bookCopyId))
            .orElseThrow(() -> new BookCopyNotFoundException(bookCopyId));
            
        // Check if there are any active borrowings
        if (bookCopyRepository.countActiveBookCopyBorrowings(bookCopy.getId()) > 0) {
            throw new InvalidBookCopyDataException("bookCopy", 
                "Cannot delete book copy that is currently borrowed");
        }
        
        bookCopy.markAsDeleted();
        bookCopyRepository.delete(bookCopy);
        
        return true;
    }
    
    /**
     * Get a book copy by ID
     * 
     * @param bookCopyId the book copy ID
     * @return the book copy
     */
    public BookCopy getBookCopyById(Long bookCopyId) {
        return bookCopyRepository.findById(BookCopyId.of(bookCopyId))
            .orElseThrow(() -> new BookCopyNotFoundException(bookCopyId));
    }
} 
package com.library.book.infrastructure.repository;

import com.library.book.domain.model.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    
    /**
     * Find all book copies for a specific book
     * 
     * @param bookId the ID of the book
     * @return list of book copies
     */
    List<BookCopy> findByBookId(Long bookId);
    
    /**
     * Check if a copy number already exists for a specific book
     * 
     * @param bookId the ID of the book
     * @param copyNumber the copy number to check
     * @return true if the copy number exists
     */
    boolean existsByBookIdAndCopyNumber(Long bookId, String copyNumber);
    
    /**
     * Count borrowings for a book copy that have not been returned
     * 
     * @param bookCopyId the ID of the book copy
     * @return count of active borrowings
     */
    long countByIdAndBorrowingsReturnDateIsNull(Long bookCopyId);
}

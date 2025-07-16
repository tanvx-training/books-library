package com.library.book.domain.specification;

import com.library.book.domain.model.book.Book;
import com.library.book.domain.repository.BookCopyRepository;
import lombok.RequiredArgsConstructor;

/**
 * Specification to check if a book has available copies
 */
@RequiredArgsConstructor
public class BookAvailabilitySpecification implements BookSpecification {
    
    private final BookCopyRepository bookCopyRepository;
    private final int minimumAvailableCopies;
    
    public BookAvailabilitySpecification(BookCopyRepository bookCopyRepository) {
        this(bookCopyRepository, 1);
    }
    
    @Override
    public boolean isSatisfiedBy(Book book) {
        long availableCopies = bookCopyRepository.countAvailableCopiesByBookId(book.getId());
        return availableCopies >= minimumAvailableCopies;
    }
}
package com.library.book.domain.specification;

import com.library.book.domain.model.book.Book;

/**
 * Specification to filter books by publication year range
 */
public class BookPublicationYearSpecification implements BookSpecification {
    
    private final Integer fromYear;
    private final Integer toYear;
    
    public BookPublicationYearSpecification(Integer fromYear, Integer toYear) {
        this.fromYear = fromYear;
        this.toYear = toYear;
    }
    
    @Override
    public boolean isSatisfiedBy(Book book) {
        Integer publicationYear = book.getPublicationYear().getValue();
        
        if (publicationYear == null) {
            return false;
        }
        
        boolean satisfiesFrom = fromYear == null || publicationYear >= fromYear;
        boolean satisfiesTo = toYear == null || publicationYear <= toYear;
        
        return satisfiesFrom && satisfiesTo;
    }
}
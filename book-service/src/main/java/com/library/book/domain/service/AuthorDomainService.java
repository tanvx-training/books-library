package com.library.book.domain.service;

import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.repository.AuthorRepository;
import com.library.book.domain.repository.BookRepository;
import com.library.book.domain.specification.AuthorSpecification;
import com.library.book.domain.specification.AuthorWithBooksSpecification;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Domain service for Author-related business logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorDomainService {
    
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    
    /**
     * Check if an author can be safely deleted
     */
    public boolean canAuthorBeDeleted(AuthorId authorId) {
        Optional<Author> authorOpt = authorRepository.findById(authorId);
        if (authorOpt.isEmpty()) {
            return false;
        }
        
        Author author = authorOpt.get();
        return author.canBeDeleted();
    }
    
    /**
     * Get authors with published books
     */
    public List<Author> getAuthorsWithBooks() {
        List<Author> allAuthors = authorRepository.findAll();
        AuthorSpecification hasBooks = new AuthorWithBooksSpecification();
        
        return allAuthors.stream()
            .filter(hasBooks::isSatisfiedBy)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get prolific authors (authors with many books)
     */
    public List<Author> getProlificAuthors(int minimumBookCount) {
        List<Author> allAuthors = authorRepository.findAll();
        AuthorSpecification isProlific = new AuthorWithBooksSpecification(minimumBookCount);
        
        return allAuthors.stream()
            .filter(isProlific::isSatisfiedBy)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Calculate author productivity score
     */
    public double calculateAuthorProductivityScore(AuthorId authorId) {
        Optional<Author> authorOpt = authorRepository.findById(authorId);
        if (authorOpt.isEmpty()) {
            return 0.0;
        }
        
        Author author = authorOpt.get();
        int bookCount = author.getBookCount();
        
        // Simple productivity calculation based on book count
        // In a real system, you might consider:
        // - Publication frequency
        // - Book popularity
        // - Recent activity
        return Math.min(bookCount * 10.0, 100.0); // Cap at 100
    }
    
    /**
     * Find similar authors based on book count
     */
    public List<Author> findSimilarAuthors(AuthorId authorId, int tolerance) {
        Optional<Author> authorOpt = authorRepository.findById(authorId);
        if (authorOpt.isEmpty()) {
            return List.of();
        }
        
        Author targetAuthor = authorOpt.get();
        int targetBookCount = targetAuthor.getBookCount();
        
        return authorRepository.findAll().stream()
            .filter(author -> !author.getId().equals(authorId))
            .filter(author -> Math.abs(author.getBookCount() - targetBookCount) <= tolerance)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get author statistics
     */
    public AuthorStatistics getAuthorStatistics(AuthorId authorId) {
        Optional<Author> authorOpt = authorRepository.findById(authorId);
        if (authorOpt.isEmpty()) {
            return null;
        }
        
        Author author = authorOpt.get();
        
        return AuthorStatistics.builder()
            .authorId(authorId.getValue())
            .authorName(author.getName().getValue())
            .totalBooks(author.getBookCount())
            .productivityScore(calculateAuthorProductivityScore(authorId))
            .canBeDeleted(author.canBeDeleted())
            .build();
    }
    
    /**
     * Validate business rules for author operations
     */
    public void validateAuthorBusinessRules(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }
        
        if (author.getName() == null || author.getName().getValue().trim().isEmpty()) {
            throw new IllegalArgumentException("Author must have a valid name");
        }
        
        // Add more business rules as needed
    }
    
    /**
     * Statistics DTO for authors
     */
    @Data
    @Builder
    public static class AuthorStatistics {
        private Long authorId;
        private String authorName;
        private int totalBooks;
        private double productivityScore;
        private boolean canBeDeleted;
    }
}

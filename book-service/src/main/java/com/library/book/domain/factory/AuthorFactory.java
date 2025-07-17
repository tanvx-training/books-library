package com.library.book.domain.factory;

import com.library.book.domain.exception.InvalidAuthorDataException;
import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorName;
import com.library.book.domain.model.author.Biography;
import com.library.book.domain.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Factory for creating Author aggregates with proper validation
 */
@Component
@RequiredArgsConstructor
public class AuthorFactory {
    
    private final AuthorRepository authorRepository;
    
    /**
     * Create a new author with validation
     */
    public Author createAuthor(AuthorCreationRequest request) {
        validateAuthorCreationRequest(request);
        
        // Check if author with same name already exists
        AuthorName authorName = AuthorName.of(request.getName());
        if (authorRepository.existsByName(authorName)) {
            throw new InvalidAuthorDataException("Author with name '" + request.getName() + "' already exists");
        }
        
        Biography biography = StringUtils.hasText(request.getBiography())
            ? Biography.of(request.getBiography())
            : Biography.empty();
        
        return Author.create(authorName, biography, request.getCreatedByKeycloakId());
    }
    
    /**
     * Create multiple authors from a list of names
     */
    public java.util.List<Author> createMultipleAuthors(
            java.util.List<String> authorNames, 
            String createdByKeycloakId) {
        
        if (authorNames == null || authorNames.isEmpty()) {
            throw new InvalidAuthorDataException("Author names list cannot be null or empty");
        }
        
        java.util.List<Author> authors = new java.util.ArrayList<>();
        
        for (String name : authorNames) {
            if (StringUtils.hasText(name)) {
                AuthorName authorName = AuthorName.of(name.trim());
                
                // Check if author already exists
                if (!authorRepository.existsByName(authorName)) {
                    Author author = Author.create(
                        authorName, 
                        Biography.empty(), 
                        createdByKeycloakId
                    );
                    authors.add(author);
                }
            }
        }
        
        return authors;
    }
    
    private void validateAuthorCreationRequest(AuthorCreationRequest request) {
        if (request == null) {
            throw new InvalidAuthorDataException("Author creation request cannot be null");
        }
        
        if (!StringUtils.hasText(request.getName())) {
            throw new InvalidAuthorDataException("Author name is required");
        }
        
        if (!StringUtils.hasText(request.getCreatedByKeycloakId())) {
            throw new InvalidAuthorDataException("Created by user ID is required");
        }
    }
    
    /**
     * Request object for author creation
     */
    @lombok.Data
    @lombok.Builder
    public static class AuthorCreationRequest {
        private String name;
        private String biography;
        private String createdByKeycloakId;
    }
}
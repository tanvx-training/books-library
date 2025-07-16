package com.library.book.domain.factory;

import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.model.book.*;
import com.library.book.domain.model.category.CategoryId;
import com.library.book.domain.model.publisher.PublisherId;
import com.library.book.domain.repository.AuthorRepository;
import com.library.book.domain.repository.CategoryRepository;
import com.library.book.domain.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for creating Book aggregates with proper validation
 */
@Component
@RequiredArgsConstructor
public class BookFactory {
    
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    
    /**
     * Create a new book with validation
     */
    public Book createBook(BookCreationRequest request) {
        // Validate required fields
        validateBookCreationRequest(request);
        
        // Validate references exist
        validateReferences(request);
        
        // Create value objects
        BookTitle title = BookTitle.of(request.getTitle());
        ISBN isbn = ISBN.of(request.getIsbn());
        PublisherId publisherId = new PublisherId(request.getPublisherId());
        
        PublicationYear publicationYear = request.getPublicationYear() != null 
            ? PublicationYear.of(request.getPublicationYear())
            : PublicationYear.empty();
            
        Description description = StringUtils.hasText(request.getDescription())
            ? Description.of(request.getDescription())
            : Description.empty();
            
        CoverImageUrl coverImageUrl = StringUtils.hasText(request.getCoverImageUrl())
            ? CoverImageUrl.of(request.getCoverImageUrl())
            : CoverImageUrl.empty();
            
        List<AuthorId> authorIds = request.getAuthorIds().stream()
            .map(AuthorId::new)
            .collect(Collectors.toList());
            
        List<CategoryId> categoryIds = request.getCategoryIds().stream()
            .map(CategoryId::new)
            .collect(Collectors.toList());
        
        return Book.create(
            title,
            isbn,
            publisherId,
            publicationYear,
            description,
            coverImageUrl,
            authorIds,
            categoryIds
        );
    }
    
    private void validateBookCreationRequest(BookCreationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Book creation request cannot be null");
        }
        
        if (!StringUtils.hasText(request.getTitle())) {
            throw new IllegalArgumentException("Book title is required");
        }
        
        if (!StringUtils.hasText(request.getIsbn())) {
            throw new IllegalArgumentException("Book ISBN is required");
        }
        
        if (request.getPublisherId() == null) {
            throw new IllegalArgumentException("Publisher ID is required");
        }
        
        if (request.getAuthorIds() == null || request.getAuthorIds().isEmpty()) {
            throw new IllegalArgumentException("At least one author is required");
        }
        
        if (request.getCategoryIds() == null || request.getCategoryIds().isEmpty()) {
            throw new IllegalArgumentException("At least one category is required");
        }
    }
    
    private void validateReferences(BookCreationRequest request) {
        // Validate publisher exists
        if (!publisherRepository.findById(new com.library.book.domain.model.publisher.PublisherId(request.getPublisherId())).isPresent()) {
            throw new IllegalArgumentException("Publisher with ID " + request.getPublisherId() + " does not exist");
        }
        
        // Validate all authors exist
        for (Long authorId : request.getAuthorIds()) {
            if (!authorRepository.findById(new AuthorId(authorId)).isPresent()) {
                throw new IllegalArgumentException("Author with ID " + authorId + " does not exist");
            }
        }
        
        // Validate all categories exist
        for (Long categoryId : request.getCategoryIds()) {
            if (!categoryRepository.findById(new CategoryId(categoryId)).isPresent()) {
                throw new IllegalArgumentException("Category with ID " + categoryId + " does not exist");
            }
        }
    }
    
    /**
     * Request object for book creation
     */
    @lombok.Data
    @lombok.Builder
    public static class BookCreationRequest {
        private String title;
        private String isbn;
        private Long publisherId;
        private Integer publicationYear;
        private String description;
        private String coverImageUrl;
        private List<Long> authorIds;
        private List<Long> categoryIds;
    }
}
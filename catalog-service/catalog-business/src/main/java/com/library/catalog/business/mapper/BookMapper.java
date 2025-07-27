package com.library.catalog.business.mapper;

import com.library.catalog.repository.entity.Book;
import com.library.catalog.repository.entity.Author;
import com.library.catalog.repository.entity.Category;
import com.library.catalog.repository.entity.Publisher;
import com.library.catalog.repository.AuthorRepository;
import com.library.catalog.repository.CategoryRepository;
import com.library.catalog.repository.PublisherRepository;
import com.library.catalog.business.dto.request.CreateBookRequest;
import com.library.catalog.business.dto.request.UpdateBookRequest;
import com.library.catalog.business.dto.response.BookResponse;
import com.library.catalog.business.dto.response.PagedBookResponse;
import com.library.catalog.business.aop.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Mapper component for converting between Book entities and DTOs.
 * Handles conversion logic for CRUD operations while maintaining consistency
 * with existing mapper patterns in the catalog service.
 * Supports public_id to internal ID resolution for relationships.
 */
@Component
public class BookMapper {

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Converts CreateBookRequest to Book entity.
     * Sets all required and optional fields from the request.
     * Resolves publisherPublicId to internal publisher_id.
     * Note: Author and category relationships are handled separately by the business layer
     * using the resolveAuthorPublicIdsToIds and resolveCategoryPublicIdsToIds methods.
     * Audit fields are handled by JPA/service layer.
     *
     * @param request the create book request
     * @return Book entity ready for persistence
     * @throws EntityNotFoundException if publisher public ID is not found
     */
    public Book toEntity(CreateBookRequest request) {
        if (request == null) {
            return null;
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublicationYear(request.getPublicationYear());
        book.setDescription(request.getDescription());
        book.setLanguage(request.getLanguage());
        book.setNumberOfPages(request.getNumberOfPages());
        book.setCoverImageUrl(request.getCoverImageUrl());
        
        // Resolve publisherPublicId to internal publisher_id
        Long publisherId = resolvePublisherPublicIdToId(request.getPublisherPublicId());
        book.setPublisherId(publisherId);
        
        // UUID is auto-generated in @PrePersist
        // audit fields (createdAt, updatedAt, createdBy, updatedBy) are handled by JPA/service layer
        
        return book;
    }

    /**
     * Updates existing Book entity with data from UpdateBookRequest.
     * Only updates fields that are not null in the request to support partial updates.
     * Resolves publisherPublicId to internal publisher_id if provided.
     * Note: Author and category relationship updates are handled separately by the business layer
     * using the resolveAuthorPublicIdsToIds and resolveCategoryPublicIdsToIds methods.
     * Audit fields (updatedAt, updatedBy) are handled by JPA/service layer.
     *
     * @param entity the existing book entity to update
     * @param request the update book request
     * @throws EntityNotFoundException if publisher public ID is provided but not found
     */
    public void updateEntity(Book entity, UpdateBookRequest request) {
        if (entity == null || request == null) {
            return;
        }

        if (request.getTitle() != null) {
            entity.setTitle(request.getTitle());
        }
        if (request.getIsbn() != null) {
            entity.setIsbn(request.getIsbn());
        }
        if (request.getPublicationYear() != null) {
            entity.setPublicationYear(request.getPublicationYear());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getLanguage() != null) {
            entity.setLanguage(request.getLanguage());
        }
        if (request.getNumberOfPages() != null) {
            entity.setNumberOfPages(request.getNumberOfPages());
        }
        if (request.getCoverImageUrl() != null) {
            entity.setCoverImageUrl(request.getCoverImageUrl());
        }
        if (request.getPublisherPublicId() != null) {
            // Resolve publisherPublicId to internal publisher_id
            Long publisherId = resolvePublisherPublicIdToId(request.getPublisherPublicId());
            entity.setPublisherId(publisherId);
        }
        // audit fields (updatedAt, updatedBy) are handled by JPA/service layer
    }

    /**
     * Converts Book entity to BookResponse DTO.
     * Basic conversion without relationship data - relationships should be populated
     * by the business layer using the overloaded method.
     *
     * @param entity the book entity
     * @return BookResponse DTO
     */
    public BookResponse toResponse(Book entity) {
        if (entity == null) {
            return null;
        }

        // Skip mapping for soft-deleted entities
        if (entity.isDeleted()) {
            return null;
        }

        BookResponse response = new BookResponse();
        response.setPublicId(entity.getPublicId());
        response.setTitle(entity.getTitle());
        response.setIsbn(entity.getIsbn());
        response.setPublicationYear(entity.getPublicationYear());
        response.setDescription(entity.getDescription());
        response.setLanguage(entity.getLanguage());
        response.setNumberOfPages(entity.getNumberOfPages());
        response.setCoverImageUrl(entity.getCoverImageUrl());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());

        return response;
    }

    /**
     * Converts Book entity to BookResponse DTO with relationship data.
     * Populates publisher, authors, and categories information using public_id references.
     *
     * @param entity the book entity
     * @param publisher the publisher entity (can be null)
     * @param authors list of author entities (can be null or empty)
     * @param categories list of category entities (can be null or empty)
     * @return BookResponse DTO with complete relationship data
     */
    public BookResponse toResponse(Book entity, Publisher publisher, List<Author> authors, List<Category> categories) {
        BookResponse response = toResponse(entity);
        if (response == null) {
            return null;
        }

        // Set publisher information with public_id
        if (publisher != null && !publisher.isDeleted()) {
            BookResponse.PublisherInfo publisherInfo = new BookResponse.PublisherInfo();
            publisherInfo.setPublicId(publisher.getPublicId());
            publisherInfo.setName(publisher.getName());
            response.setPublisher(publisherInfo);
        }

        // Set authors information with public_id, filtering out deleted authors
        if (authors != null && !authors.isEmpty()) {
            List<BookResponse.AuthorInfo> authorInfos = authors.stream()
                    .filter(author -> !author.isDeleted())
                    .map(author -> {
                        BookResponse.AuthorInfo authorInfo = new BookResponse.AuthorInfo();
                        authorInfo.setPublicId(author.getPublicId());
                        authorInfo.setName(author.getName());
                        return authorInfo;
                    })
                    .collect(Collectors.toList());
            response.setAuthors(authorInfos);
        }

        // Set categories information with public_id, filtering out deleted categories
        if (categories != null && !categories.isEmpty()) {
            List<BookResponse.CategoryInfo> categoryInfos = categories.stream()
                    .filter(category -> !category.isDeleted())
                    .map(category -> {
                        BookResponse.CategoryInfo categoryInfo = new BookResponse.CategoryInfo();
                        categoryInfo.setPublicId(category.getPublicId());
                        categoryInfo.setName(category.getName());
                        return categoryInfo;
                    })
                    .collect(Collectors.toList());
            response.setCategories(categoryInfos);
        }

        return response;
    }

    /**
     * Converts list of Book entities to list of BookResponse DTOs.
     * Basic conversion without relationship data, filtering out soft-deleted entities.
     *
     * @param entities list of book entities
     * @return list of BookResponse DTOs
     */
    public List<BookResponse> toResponseList(List<Book> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toResponse)
                .filter(response -> response != null) // Filter out null responses from soft-deleted entities
                .collect(Collectors.toList());
    }

    /**
     * Converts Page of Book entities to PagedBookResponse DTO.
     * Basic conversion without relationship data - for use cases where
     * relationship data is not needed (e.g., listing operations).
     * Filters out soft-deleted entities.
     *
     * @param page the page of book entities
     * @return PagedBookResponse DTO with pagination metadata
     */
    public PagedBookResponse toPagedResponse(Page<Book> page) {
        if (page == null) {
            return null;
        }

        PagedBookResponse response = new PagedBookResponse();
        
        // Convert content, filtering out null responses from soft-deleted entities
        List<BookResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .filter(bookResponse -> bookResponse != null)
                .collect(Collectors.toList());
        response.setContent(content);
        
        // Set pagination metadata - these values come from the database query
        // which already filters out soft-deleted records, so they remain accurate
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());

        return response;
    }

    /**
     * Converts Page of Book entities to PagedBookResponse DTO with relationship data.
     * This method should be used when relationship data is needed for each book in the page.
     * The business layer should provide the relationship data maps.
     *
     * @param page the page of book entities
     * @param publisherMap map of publisher ID to Publisher entity
     * @param authorsMap map of book ID to list of Author entities
     * @param categoriesMap map of book ID to list of Category entities
     * @return PagedBookResponse DTO with complete relationship data
     */
    public PagedBookResponse toPagedResponse(Page<Book> page, 
                                           java.util.Map<Long, Publisher> publisherMap,
                                           java.util.Map<Long, List<Author>> authorsMap,
                                           java.util.Map<Long, List<Category>> categoriesMap) {
        if (page == null) {
            return null;
        }

        PagedBookResponse response = new PagedBookResponse();
        
        // Convert content with relationship data
        List<BookResponse> content = page.getContent().stream()
                .map(book -> {
                    Publisher publisher = publisherMap != null ? publisherMap.get(book.getPublisherId()) : null;
                    List<Author> authors = authorsMap != null ? authorsMap.get(book.getId()) : null;
                    List<Category> categories = categoriesMap != null ? categoriesMap.get(book.getId()) : null;
                    return toResponse(book, publisher, authors, categories);
                })
                .collect(Collectors.toList());
        response.setContent(content);
        
        // Set pagination metadata
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());

        return response;
    }

    /**
     * Resolves publisher public ID to internal publisher ID.
     * Uses optimized repository method when available for better performance.
     *
     * @param publisherPublicId the publisher public ID
     * @return the internal publisher ID
     * @throws EntityNotFoundException if publisher is not found
     */
    public Long resolvePublisherPublicIdToId(UUID publisherPublicId) {
        if (publisherPublicId == null) {
            throw EntityNotFoundException.forPublicId("Publisher", null);
        }

        // Use optimized method if available, otherwise fall back to entity lookup
        return publisherRepository.findPublisherIdByPublicId(publisherPublicId)
                .orElseThrow(() -> EntityNotFoundException.forPublicId("Publisher", publisherPublicId));
    }

    /**
     * Resolves list of author public IDs to list of internal author IDs.
     * Validates that all authors exist and are not soft-deleted.
     *
     * @param authorPublicIds the list of author public IDs
     * @return the list of internal author IDs
     * @throws EntityNotFoundException if any author is not found
     */
    public List<Long> resolveAuthorPublicIdsToIds(List<UUID> authorPublicIds) {
        if (authorPublicIds == null || authorPublicIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> authorIds = new ArrayList<>();
        for (UUID authorPublicId : authorPublicIds) {
            Author author = authorRepository.findByPublicIdAndDeletedAtIsNull(authorPublicId)
                    .orElseThrow(() -> EntityNotFoundException.forPublicId("Author", authorPublicId));
            authorIds.add(author.getId());
        }
        return authorIds;
    }

    /**
     * Batch resolves multiple author public IDs to a map of public ID to internal ID.
     * More efficient than individual lookups when resolving many authors.
     *
     * @param authorPublicIds the list of author public IDs
     * @return map of author public ID to internal ID
     * @throws EntityNotFoundException if any author is not found
     */
    public java.util.Map<UUID, Long> batchResolveAuthorPublicIdsToIds(List<UUID> authorPublicIds) {
        if (authorPublicIds == null || authorPublicIds.isEmpty()) {
            return new java.util.HashMap<>();
        }

        java.util.Map<UUID, Long> resultMap = new java.util.HashMap<>();
        for (UUID authorPublicId : authorPublicIds) {
            Author author = authorRepository.findByPublicIdAndDeletedAtIsNull(authorPublicId)
                    .orElseThrow(() -> EntityNotFoundException.forPublicId("Author", authorPublicId));
            resultMap.put(authorPublicId, author.getId());
        }
        return resultMap;
    }

    /**
     * Resolves list of category public IDs to list of internal category IDs.
     * Validates that all categories exist and are not soft-deleted.
     *
     * @param categoryPublicIds the list of category public IDs
     * @return the list of internal category IDs
     * @throws EntityNotFoundException if any category is not found
     */
    public List<Long> resolveCategoryPublicIdsToIds(List<UUID> categoryPublicIds) {
        if (categoryPublicIds == null || categoryPublicIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> categoryIds = new ArrayList<>();
        for (UUID categoryPublicId : categoryPublicIds) {
            Category category = categoryRepository.findByPublicIdAndDeletedAtIsNull(categoryPublicId)
                    .orElseThrow(() -> EntityNotFoundException.forPublicId("Category", categoryPublicId));
            categoryIds.add(category.getId());
        }
        return categoryIds;
    }

    /**
     * Batch resolves multiple category public IDs to a map of public ID to internal ID.
     * More efficient than individual lookups when resolving many categories.
     *
     * @param categoryPublicIds the list of category public IDs
     * @return map of category public ID to internal ID
     * @throws EntityNotFoundException if any category is not found
     */
    public java.util.Map<UUID, Long> batchResolveCategoryPublicIdsToIds(List<UUID> categoryPublicIds) {
        if (categoryPublicIds == null || categoryPublicIds.isEmpty()) {
            return new java.util.HashMap<>();
        }

        java.util.Map<UUID, Long> resultMap = new java.util.HashMap<>();
        for (UUID categoryPublicId : categoryPublicIds) {
            Category category = categoryRepository.findByPublicIdAndDeletedAtIsNull(categoryPublicId)
                    .orElseThrow(() -> EntityNotFoundException.forPublicId("Category", categoryPublicId));
            resultMap.put(categoryPublicId, category.getId());
        }
        return resultMap;
    }

    /**
     * Loads publisher entity by internal ID for relationship population.
     *
     * @param publisherId the internal publisher ID
     * @return the publisher entity or null if not found or deleted
     */
    public Publisher loadPublisherById(Long publisherId) {
        if (publisherId == null) {
            return null;
        }

        return publisherRepository.findById(publisherId)
                .filter(publisher -> !publisher.isDeleted())
                .orElse(null);
    }

    /**
     * Loads author entities by book ID for relationship population.
     * This method should be called by the business layer with proper book-author relationship data.
     *
     * @param authorIds the list of internal author IDs
     * @return the list of author entities, filtering out deleted ones
     */
    public List<Author> loadAuthorsByIds(List<Long> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return new ArrayList<>();
        }

        return authorRepository.findAllById(authorIds).stream()
                .filter(author -> !author.isDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Loads category entities by book ID for relationship population.
     * This method should be called by the business layer with proper book-category relationship data.
     *
     * @param categoryIds the list of internal category IDs
     * @return the list of category entities, filtering out deleted ones
     */
    public List<Category> loadCategoriesByIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }

        return categoryRepository.findAllById(categoryIds).stream()
                .filter(category -> !category.isDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Validates that all provided author public IDs exist and are not soft-deleted.
     * This method can be used for validation without needing to resolve to internal IDs.
     *
     * @param authorPublicIds the list of author public IDs to validate
     * @throws EntityNotFoundException if any author is not found
     */
    public void validateAuthorPublicIds(List<UUID> authorPublicIds) {
        if (authorPublicIds == null || authorPublicIds.isEmpty()) {
            return;
        }

        for (UUID authorPublicId : authorPublicIds) {
            if (!authorRepository.existsByPublicIdAndDeletedAtIsNull(authorPublicId)) {
                throw EntityNotFoundException.forPublicId("Author", authorPublicId);
            }
        }
    }

    /**
     * Validates that all provided category public IDs exist and are not soft-deleted.
     * This method can be used for validation without needing to resolve to internal IDs.
     *
     * @param categoryPublicIds the list of category public IDs to validate
     * @throws EntityNotFoundException if any category is not found
     */
    public void validateCategoryPublicIds(List<UUID> categoryPublicIds) {
        if (categoryPublicIds == null || categoryPublicIds.isEmpty()) {
            return;
        }

        for (UUID categoryPublicId : categoryPublicIds) {
            if (!categoryRepository.existsByPublicIdAndDeletedAtIsNull(categoryPublicId)) {
                throw EntityNotFoundException.forPublicId("Category", categoryPublicId);
            }
        }
    }

    /**
     * Validates that the provided publisher public ID exists and is not soft-deleted.
     * This method can be used for validation without needing to resolve to internal ID.
     *
     * @param publisherPublicId the publisher public ID to validate
     * @throws EntityNotFoundException if publisher is not found
     */
    public void validatePublisherPublicId(UUID publisherPublicId) {
        if (publisherPublicId == null) {
            throw EntityNotFoundException.forPublicId("Publisher", null);
        }

        if (!publisherRepository.existsByPublicIdAndDeletedAtIsNull(publisherPublicId)) {
            throw EntityNotFoundException.forPublicId("Publisher", publisherPublicId);
        }
    }
}
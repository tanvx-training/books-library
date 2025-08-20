package com.library.catalog.service;

import com.library.catalog.dto.request.CreateBookRequest;
import com.library.catalog.dto.request.UpdateBookRequest;
import com.library.catalog.dto.response.BookResponse;
import com.library.catalog.dto.response.PagedBookResponse;
import com.library.catalog.aop.EntityNotFoundException;
import com.library.catalog.repository.Author;
import com.library.catalog.repository.AuthorRepository;
import com.library.catalog.repository.Book;
import com.library.catalog.repository.Category;
import com.library.catalog.repository.CategoryRepository;
import com.library.catalog.repository.Publisher;
import com.library.catalog.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final PublisherRepository publisherRepository;

    private final AuthorRepository authorRepository;

    private final CategoryRepository categoryRepository;

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

    public List<BookResponse> toResponseList(List<Book> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toResponse)
                .filter(Objects::nonNull) // Filter out null responses from soft-deleted entities
                .collect(Collectors.toList());
    }

    public PagedBookResponse toPagedResponse(Page<Book> page) {
        if (page == null) {
            return null;
        }

        PagedBookResponse response = new PagedBookResponse();

        // Convert content, filtering out null responses from soft-deleted entities
        List<BookResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .filter(Objects::nonNull)
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

    public PagedBookResponse toPagedResponse(Page<Book> page,
                                             Map<Long, Publisher> publisherMap,
                                             Map<Long, List<Author>> authorsMap,
                                             Map<Long, List<Category>> categoriesMap) {
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

    public Long resolvePublisherPublicIdToId(UUID publisherPublicId) {
        if (publisherPublicId == null) {
            throw EntityNotFoundException.forPublicId("Publisher", null);
        }

        // Use optimized method if available, otherwise fall back to entity lookup
        return publisherRepository.findPublisherIdByPublicId(publisherPublicId)
                .orElseThrow(() -> EntityNotFoundException.forPublicId("Publisher", publisherPublicId));
    }
}
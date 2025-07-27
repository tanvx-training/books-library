package com.library.catalog.business.impl;

import com.library.catalog.business.BookBusiness;
import com.library.catalog.business.dto.request.BookSearchRequest;
import com.library.catalog.business.dto.request.CreateBookRequest;
import com.library.catalog.business.dto.request.UpdateBookRequest;
import com.library.catalog.business.dto.response.BookResponse;
import com.library.catalog.business.dto.response.PagedBookResponse;
import com.library.catalog.business.kafka.publisher.AuditService;
import com.library.catalog.business.mapper.BookMapper;
import com.library.catalog.repository.AuthorRepository;
import com.library.catalog.repository.BookAuthorRepository;
import com.library.catalog.repository.BookCategoryRepository;
import com.library.catalog.repository.BookRepository;
import com.library.catalog.repository.CategoryRepository;
import com.library.catalog.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BookBusinessImpl implements BookBusiness {

    private final BookRepository bookRepository;
    private final BookAuthorRepository bookAuthorRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final AuditService auditService;

    @Override
    @Transactional
    public BookResponse createBook(CreateBookRequest request, String currentUser) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookByPublicId(UUID publicId) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedBookResponse searchBooks(BookSearchRequest request) {
        return null;
    }

    @Override
    @Transactional
    public BookResponse updateBook(UUID publicId, UpdateBookRequest request, String currentUser) {
        return null;
    }

    @Override
    @Transactional
    public void deleteBook(UUID publicId, String currentUser) {
    }

    private boolean hasMultipleCriteria(BookSearchRequest criteria) {

        return StringUtils.hasText(criteria.getTitle())
                || StringUtils.hasText(criteria.getIsbn())
                || Objects.nonNull(criteria.getPublicationYear())
                || StringUtils.hasText(criteria.getLanguage())
                || Objects.nonNull(criteria.getPublisherPublicId())
                || StringUtils.hasText(criteria.getAuthorPublicId())
                || Objects.nonNull(criteria.getCategoryPublicId());
    }
}
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
import com.library.catalog.repository.entity.Author;
import com.library.catalog.repository.entity.Book;
import com.library.catalog.repository.entity.BookAuthor;
import com.library.catalog.repository.entity.BookCategory;
import com.library.catalog.repository.entity.Category;
import com.library.catalog.repository.entity.Publisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

        // Perform search with pagination
        Page<Book> bookPage = bookRepository.searchBooks(
                request.getTitle(),
                request.getIsbn(),
                request.getPublisherName(),
                request.getAuthorName(),
                request.getCategoryName(),
                request.getPublicationYear(),
                request.getLanguage(),
                request.toPageable()
        );

        // Convert to response with relationship data
        return bookMapper.toPagedResponse(bookPage,
                loadPublishersForBooks(bookPage.getContent()),
                loadAuthorsForBooks(bookPage.getContent()),
                loadCategoriesForBooks(bookPage.getContent())
        );
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

    private Map<Long, Publisher> loadPublishersForBooks(List<Book> books) {

        if (books == null || books.isEmpty()) {
            return new HashMap<>();
        }
        Set<Long> publisherIds = books.stream().map(Book::getPublisherId).collect(Collectors.toSet());
        return publisherRepository.findAllById(publisherIds).stream()
                .filter(publisher -> !publisher.isDeleted())
                .collect(Collectors.toMap(Publisher::getId,
                        publisher -> publisher
                ));
    }

    private Map<Long, List<Author>> loadAuthorsForBooks(List<Book> books) {

        if (books == null || books.isEmpty()) {
            return new java.util.HashMap<>();
        }
        Set<Long> bookIds = books.stream().map(Book::getId).collect(Collectors.toSet());
        // Get book-author relationships
        List<BookAuthor> bookAuthors = bookAuthorRepository.findByBookIdIn(new ArrayList<>(bookIds));
        // Get author IDs
        Set<Long> authorIds = bookAuthors.stream().map(BookAuthor::getAuthorId).collect(Collectors.toSet());
        // Load authors
        Map<Long, Author> authorsMap = authorRepository.findAllById(new ArrayList<>(authorIds)).stream()
                .filter(author -> !author.isDeleted())
                .collect(Collectors.toMap(Author::getId, author -> author));
        // Group by book ID
        return bookAuthors.stream()
                .filter(ba -> authorsMap.containsKey(ba.getAuthorId()))
                .collect(Collectors.groupingBy(BookAuthor::getBookId,
                        Collectors.mapping(ba -> authorsMap.get(ba.getAuthorId()), Collectors.toList())
                ));
    }

    private Map<Long, List<Category>> loadCategoriesForBooks(List<Book> books) {

        if (books == null || books.isEmpty()) {
            return new HashMap<>();
        }
        Set<Long> bookIds = books.stream().map(Book::getId).collect(java.util.stream.Collectors.toSet());
        // Get book-category relationships
        List<BookCategory> bookCategories = bookCategoryRepository.findByBookIdIn(new ArrayList<>(bookIds));

        // Get category IDs
        Set<Long> categoryIds = bookCategories.stream().map(BookCategory::getCategoryId).collect(java.util.stream.Collectors.toSet());

        // Load categories
        Map<Long, Category> categoriesMap = categoryRepository.findAllById(new ArrayList<>(categoryIds)).stream()
                .filter(category -> !category.isDeleted())
                .collect(Collectors.toMap(Category::getId, category -> category));
        // Group by book ID
        return bookCategories.stream()
                .filter(bc -> categoriesMap.containsKey(bc.getCategoryId()))
                .collect(Collectors.groupingBy(BookCategory::getBookId,
                        Collectors.mapping(bc -> categoriesMap.get(bc.getCategoryId()), java.util.stream.Collectors.toList())
                ));
    }
}
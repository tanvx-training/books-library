package com.library.catalog.business.impl;

import com.library.catalog.business.BookBusiness;
import com.library.catalog.business.aop.exception.EntityNotFoundException;
import com.library.catalog.business.dto.request.BookSearchRequest;
import com.library.catalog.business.dto.request.CreateBookRequest;
import com.library.catalog.business.dto.request.UpdateBookRequest;
import com.library.catalog.business.dto.response.BookDetailResponse;
import com.library.catalog.business.dto.response.BookResponse;
import com.library.catalog.business.dto.response.PagedBookResponse;
import com.library.catalog.business.kafka.publisher.AuditService;
import com.library.catalog.business.mapper.BookMapper;
import com.library.catalog.repository.AuthorRepository;
import com.library.catalog.repository.BookAuthorRepository;
import com.library.catalog.repository.BookCategoryRepository;
import com.library.catalog.repository.BookCopyRepository;
import com.library.catalog.repository.BookRepository;
import com.library.catalog.repository.CategoryRepository;
import com.library.catalog.repository.PublisherRepository;
import com.library.catalog.repository.entity.Author;
import com.library.catalog.repository.entity.Book;
import com.library.catalog.repository.entity.BookAuthor;
import com.library.catalog.repository.entity.BookCategory;
import com.library.catalog.repository.entity.BookCopy;
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
    private final BookCopyRepository bookCopyRepository;
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
    public BookDetailResponse getBookDetail(UUID publicId) {
        // Find the book by public ID
        Book book = bookRepository.findByPublicIdAndDeletedAtIsNull(publicId)
                .orElseThrow(() -> EntityNotFoundException.forPublicId("Book", publicId));

        // Load related entities
        Publisher publisher = loadPublisherById(book.getPublisherId());
        List<Author> authors = loadAuthorsByBookId(book.getId());
        List<Category> categories = loadCategoriesByBookId(book.getId());
        List<BookCopy> copies = bookCopyRepository.findByBookIdAndDeletedAtIsNull(book.getId());

        // Build the detailed response
        BookDetailResponse response = new BookDetailResponse();
        response.setId(book.getPublicId());
        response.setTitle(book.getTitle());
        response.setAuthor(authors.stream()
                .map(Author::getName)
                .collect(Collectors.joining(", ")));
        response.setIsbn(book.getIsbn());
        response.setCategory(categories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", ")));
        response.setStatus(determineBookStatus(copies));
        response.setCopies(mapBookCopies(copies));
        response.setPublisher(publisher != null ? publisher.getName() : null);
        response.setPublicationYear(book.getPublicationYear());
        response.setDescription(book.getDescription());
        response.setLanguage(book.getLanguage());
        response.setPages(book.getNumberOfPages());
        response.setCreatedAt(book.getCreatedAt());
        response.setUpdatedAt(book.getUpdatedAt());

        return response;
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

    private Publisher loadPublisherById(Long publisherId) {
        if (publisherId == null) {
            return null;
        }
        return publisherRepository.findById(publisherId)
                .filter(publisher -> !publisher.isDeleted())
                .orElse(null);
    }

    private List<Author> loadAuthorsByBookId(Long bookId) {
        List<BookAuthor> bookAuthors = bookAuthorRepository.findByBookId(bookId);
        if (bookAuthors.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> authorIds = bookAuthors.stream()
                .map(BookAuthor::getAuthorId)
                .collect(Collectors.toSet());

        return authorRepository.findAllById(new ArrayList<>(authorIds)).stream()
                .filter(author -> !author.isDeleted())
                .collect(Collectors.toList());
    }

    private List<Category> loadCategoriesByBookId(Long bookId) {
        List<BookCategory> bookCategories = bookCategoryRepository.findByBookId(bookId);
        if (bookCategories.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> categoryIds = bookCategories.stream()
                .map(BookCategory::getCategoryId)
                .collect(Collectors.toSet());

        return categoryRepository.findAllById(new ArrayList<>(categoryIds)).stream()
                .filter(category -> !category.isDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Determine overall book status based on copies
     */
    private String determineBookStatus(List<BookCopy> copies) {
        if (copies.isEmpty()) {
            return "unavailable";
        }

        boolean hasAvailable = copies.stream().anyMatch(BookCopy::isAvailable);
        if (hasAvailable) {
            return "available";
        }

        boolean hasBorrowed = copies.stream().anyMatch(BookCopy::isBorrowed);
        if (hasBorrowed) {
            return "borrowed";
        }

        return "unavailable";
    }

    /**
     * Map book copies to response DTOs
     */
    private List<BookDetailResponse.BookCopyInfo> mapBookCopies(List<BookCopy> copies) {
        return copies.stream()
                .map(this::mapBookCopy)
                .collect(Collectors.toList());
    }

    /**
     * Map single book copy to response DTO
     */
    private BookDetailResponse.BookCopyInfo mapBookCopy(BookCopy copy) {
        BookDetailResponse.BookCopyInfo copyInfo = new BookDetailResponse.BookCopyInfo();
        copyInfo.setId(copy.getPublicId());
        copyInfo.setCopyNumber(copy.getCopyNumber());
        copyInfo.setStatus(copy.getStatus().name().toLowerCase());
        copyInfo.setCondition(copy.getCondition() != null ? 
                mapConditionToDisplayName(copy.getCondition()) : null);
        copyInfo.setLocation(copy.getLocation());
        copyInfo.setBarcode(generateBarcode(copy));
        copyInfo.setCreatedAt(copy.getCreatedAt());
        copyInfo.setUpdatedAt(copy.getUpdatedAt());
        return copyInfo;
    }

    /**
     * Map condition enum to display name
     */
    private String mapConditionToDisplayName(com.library.catalog.repository.enums.BookCopyCondition condition) {
        return switch (condition) {
            case NEW -> "excellent";
            case GOOD -> "good";
            case FAIR -> "fair";
            case POOR -> "poor";
            case DAMAGED -> "damaged";
        };
    }

    /**
     * Generate barcode for book copy (placeholder implementation)
     */
    private String generateBarcode(BookCopy copy) {
        // Simple barcode generation based on copy number
        // In real implementation, this might be stored in database or generated differently
        return "GB" + String.format("%06d", copy.getId());
    }
}
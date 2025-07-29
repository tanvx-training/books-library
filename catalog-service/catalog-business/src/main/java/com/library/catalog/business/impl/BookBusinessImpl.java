package com.library.catalog.business.impl;

import com.library.catalog.business.BookBusiness;
import com.library.catalog.business.aop.exception.EntityNotFoundException;
import com.library.catalog.business.dto.request.BookSearchRequest;
import com.library.catalog.business.dto.request.CreateBookRequest;
import com.library.catalog.business.dto.request.CreateBookWithCopiesRequest;
import com.library.catalog.business.dto.request.UpdateBookWithCopiesRequest;
import com.library.catalog.business.dto.response.BookDetailResponse;
import com.library.catalog.business.dto.response.BookResponse;
import com.library.catalog.business.dto.response.PagedBookResponse;
import com.library.catalog.business.kafka.publisher.AuditService;
import com.library.catalog.business.mapper.BookMapper;
import com.library.catalog.business.util.EntityExceptionUtils;
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
import com.library.catalog.repository.enums.BookCopyCondition;
import com.library.catalog.repository.enums.BookCopyStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    @Transactional
    public BookDetailResponse createBookWithCopies(CreateBookWithCopiesRequest request, String currentUser) {

        EntityExceptionUtils.requireNoDuplicate(bookRepository.existsByIsbnAndDeletedAtIsNull(request.getIsbn()),
                "Book", "ISBN", request.getIsbn());
        // Step 1: Validate and load related entities by internal IDs
        Publisher publisher = loadPublisherById(request.getPublisherId());
        List<Author> authors = loadAuthorsByIds(request.getAuthorIds());
        List<Category> categories = loadCategoriesByIds(request.getCategoryIds());

        // Step 2: Create the book entity
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublicationYear(request.getPublicationYear());
        book.setDescription(request.getDescription());
        book.setLanguage(request.getLanguage());
        book.setNumberOfPages(request.getPages());
        book.setPublisherId(publisher.getId());
        // Save the book
        book = bookRepository.save(book);
        // Step 3: Create book-author relationships
        createBookAuthorRelationships(book.getId(), authors);
        // Step 4: Create book-category relationships
        createBookCategoryRelationships(book.getId(), categories);
        // Step 5: Create book copies
        List<BookCopy> copies = createBookCopies(book.getId(), request.getCopies());
        // Step 6: Publish audit event
        auditService.publishCreateEvent("Book", book.getPublicId().toString(), book, currentUser);
        // Step 7: Return detailed response
        return buildBookDetailResponse(book, publisher, authors, categories, copies);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDetailResponse getBookDetail(UUID publicId) {
        // Find the book by public ID
        Book book = bookRepository.findByPublicIdAndDeletedAtIsNull(publicId).orElseThrow(() -> EntityNotFoundException.forPublicId("Book", publicId));

        // Load related entities
        Publisher publisher = loadPublisherById(book.getPublisherId());
        List<Author> authors = loadAuthorsByBookId(book.getId());
        List<Category> categories = loadCategoriesByBookId(book.getId());
        List<BookCopy> copies = bookCopyRepository.findByBookIdAndDeletedAtIsNull(book.getId());
        return buildBookDetailResponse(book, publisher, authors, categories, copies);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedBookResponse searchBooks(BookSearchRequest request) {

        // Perform search with pagination
        Page<Book> bookPage = bookRepository.searchBooks(request.getTitle(), request.getIsbn(), request.getPublisherName(), request.getAuthorName(), request.getCategoryName(), request.getPublicationYear(), request.getLanguage(), request.toPageable());

        // Convert to response with relationship data
        return bookMapper.toPagedResponse(bookPage, loadPublishersForBooks(bookPage.getContent()), loadAuthorsForBooks(bookPage.getContent()), loadCategoriesForBooks(bookPage.getContent()));
    }

    @Override
    @Transactional
    public BookDetailResponse updateBookWithCopies(UUID publicId, UpdateBookWithCopiesRequest request, String currentUser) {
        // Step 1: Find existing book
        Book existingBook = bookRepository.findByPublicIdAndDeletedAtIsNull(publicId)
                .orElseThrow(() -> EntityNotFoundException.forPublicId("Book", publicId));

        // Step 2: Validate ISBN uniqueness (excluding current book)
        if (!existingBook.getIsbn().equals(request.getIsbn())) {
            EntityExceptionUtils.requireNoDuplicate(
                    bookRepository.existsByIsbnAndDeletedAtIsNullAndPublicIdNot(request.getIsbn(), publicId),
                    "Book", "ISBN", request.getIsbn()
            );
        }

        Book oldBook = getOldBook(existingBook);

        // Step 5: Update book entity
        existingBook.setTitle(request.getTitle());
        existingBook.setIsbn(request.getIsbn());
        existingBook.setPublicationYear(request.getPublicationYear());
        existingBook.setDescription(request.getDescription());
        existingBook.setLanguage(request.getLanguage());
        existingBook.setNumberOfPages(request.getPages());

        Publisher publisher = loadPublisherById(request.getPublisherId());
        existingBook.setPublisherId(publisher.getId());

        // Save updated book
        existingBook = bookRepository.save(existingBook);

        // Step 6: Update book-author relationships
        List<Author> authors = loadAuthorsByIds(request.getAuthorIds());
        updateBookAuthorRelationships(existingBook.getId(), authors);

        // Step 7: Update book-category relationships
        List<Category> categories = loadCategoriesByIds(request.getCategoryIds());
        updateBookCategoryRelationships(existingBook.getId(), categories);

        // Step 8: Update book copies
        List<BookCopy> updatedCopies = updateBookCopies(existingBook.getId(), request.getCopies());

        // Step 9: Publish audit event
        auditService.publishUpdateEvent("Book", existingBook.getPublicId().toString(),
                oldBook, existingBook, currentUser);

        // Step 10: Return detailed response
        return buildBookDetailResponse(existingBook, publisher, authors, categories, updatedCopies);
    }

    @Override
    @Transactional
    public void deleteBook(UUID publicId, String currentUser) {
        // Step 1: Find existing book
        Book existingBook = bookRepository.findByPublicIdAndDeletedAtIsNull(publicId)
                .orElseThrow(() -> EntityNotFoundException.forPublicId("Book", publicId));

        // Step 2: Check if book has any active book copies (business rule validation)
        List<BookCopy> activeCopies = bookCopyRepository.findByBookIdAndDeletedAtIsNull(existingBook.getId());
        if (!activeCopies.isEmpty()) {
            // Check if any copies are currently borrowed or reserved
            boolean hasActiveCopies = activeCopies.stream()
                    .anyMatch(copy -> copy.isBorrowed() || copy.isReserved());
            
            if (hasActiveCopies) {
                throw new IllegalStateException("Cannot delete book with active copies that are borrowed or reserved");
            }
        }

        // Step 3: Store old values for audit
        Book oldBook = getOldBook(existingBook);

        // Step 4: Perform soft delete on the book
        existingBook.markAsDeleted();
        bookRepository.save(existingBook);

        // Step 5: Soft delete all associated book copies
        for (BookCopy copy : activeCopies) {
            copy.markAsDeleted();
            bookCopyRepository.save(copy);
        }

        // Step 6: Note: We don't delete book-author and book-category relationships
        // as they are junction table records and should be preserved for audit purposes

        // Step 7: Publish audit event
        auditService.publishDeleteEvent("Book", existingBook.getPublicId().toString(), 
                                       oldBook, currentUser);
    }

    private static Book getOldBook(Book existingBook) {
        Book oldBook = new Book();
        oldBook.setId(existingBook.getId());
        oldBook.setPublicId(existingBook.getPublicId());
        oldBook.setTitle(existingBook.getTitle());
        oldBook.setIsbn(existingBook.getIsbn());
        oldBook.setPublicationYear(existingBook.getPublicationYear());
        oldBook.setDescription(existingBook.getDescription());
        oldBook.setLanguage(existingBook.getLanguage());
        oldBook.setNumberOfPages(existingBook.getNumberOfPages());
        oldBook.setPublisherId(existingBook.getPublisherId());
        return oldBook;
    }

    private Map<Long, Publisher> loadPublishersForBooks(List<Book> books) {

        if (books == null || books.isEmpty()) {
            return new HashMap<>();
        }
        Set<Long> publisherIds = books.stream().map(Book::getPublisherId).collect(Collectors.toSet());
        return publisherRepository.findAllById(publisherIds)
                .stream()
                .filter(publisher -> !publisher.isDeleted())
                .collect(Collectors.toMap(Publisher::getId, publisher -> publisher));
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
        Map<Long, Author> authorsMap = authorRepository.findAllById(new ArrayList<>(authorIds)).stream().filter(author -> !author.isDeleted()).collect(Collectors.toMap(Author::getId, author -> author));
        // Group by book ID
        return bookAuthors.stream().filter(ba -> authorsMap.containsKey(ba.getAuthorId())).collect(Collectors.groupingBy(BookAuthor::getBookId, Collectors.mapping(ba -> authorsMap.get(ba.getAuthorId()), Collectors.toList())));
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
        Map<Long, Category> categoriesMap = categoryRepository.findAllById(new ArrayList<>(categoryIds)).stream().filter(category -> !category.isDeleted()).collect(Collectors.toMap(Category::getId, category -> category));
        // Group by book ID
        return bookCategories.stream().filter(bc -> categoriesMap.containsKey(bc.getCategoryId())).collect(Collectors.groupingBy(BookCategory::getBookId, Collectors.mapping(bc -> categoriesMap.get(bc.getCategoryId()), java.util.stream.Collectors.toList())));
    }

    private Publisher loadPublisherById(Long publisherId) {
        if (publisherId == null) {
            return null;
        }
        return publisherRepository.findById(publisherId)
                .filter(publisher -> !publisher.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Publisher with ID " + publisherId + " not found"));
    }

    private List<Author> loadAuthorsByBookId(Long bookId) {
        List<BookAuthor> bookAuthors = bookAuthorRepository.findByBookId(bookId);
        if (bookAuthors.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> authorIds = bookAuthors.stream().map(BookAuthor::getAuthorId).collect(Collectors.toSet());

        return authorRepository.findAllById(new ArrayList<>(authorIds)).stream().filter(author -> !author.isDeleted()).collect(Collectors.toList());
    }

    private List<Category> loadCategoriesByBookId(Long bookId) {
        List<BookCategory> bookCategories = bookCategoryRepository.findByBookId(bookId);
        if (bookCategories.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> categoryIds = bookCategories.stream().map(BookCategory::getCategoryId).collect(Collectors.toSet());

        return categoryRepository.findAllById(new ArrayList<>(categoryIds)).stream().filter(category -> !category.isDeleted()).collect(Collectors.toList());
    }

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

    private List<BookDetailResponse.BookCopyInfo> mapBookCopies(List<BookCopy> copies) {
        return copies.stream().map(this::mapBookCopy).collect(Collectors.toList());
    }

    private BookDetailResponse.BookCopyInfo mapBookCopy(BookCopy copy) {
        BookDetailResponse.BookCopyInfo copyInfo = new BookDetailResponse.BookCopyInfo();
        copyInfo.setId(copy.getPublicId());
        copyInfo.setCopyNumber(copy.getCopyNumber());
        copyInfo.setStatus(copy.getStatus().name().toLowerCase());
        copyInfo.setCondition(copy.getCondition() != null ? mapConditionToDisplayName(copy.getCondition()) : null);
        copyInfo.setLocation(copy.getLocation());
        copyInfo.setBarcode(generateBarcode(copy));
        copyInfo.setCreatedAt(copy.getCreatedAt());
        copyInfo.setUpdatedAt(copy.getUpdatedAt());
        return copyInfo;
    }

    private String mapConditionToDisplayName(BookCopyCondition condition) {
        return switch (condition) {
            case NEW -> "excellent";
            case GOOD -> "good";
            case FAIR -> "fair";
            case POOR -> "poor";
            case DAMAGED -> "damaged";
        };
    }

    private String generateBarcode(BookCopy copy) {
        return "GB" + String.format("%06d", copy.getId());
    }

    private List<Author> loadAuthorsByIds(List<Long> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            throw new IllegalArgumentException("Author IDs cannot be empty");
        }

        List<Author> authors = authorRepository.findAllById(authorIds).stream().filter(author -> !author.isDeleted()).collect(Collectors.toList());

        // Validate that all authors were found
        if (authors.size() != authorIds.size()) {
            Set<Long> foundIds = authors.stream().map(Author::getId).collect(Collectors.toSet());
            List<Long> missingIds = authorIds.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new EntityNotFoundException("Author(s) with ID(s) " + missingIds + " not found");
        }

        return authors;
    }

    private List<Category> loadCategoriesByIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new IllegalArgumentException("Category IDs cannot be empty");
        }

        List<Category> categories = categoryRepository.findAllById(categoryIds)
                .stream()
                .filter(category -> !category.isDeleted())
                .collect(Collectors.toList());

        // Validate that all categories were found
        if (categories.size() != categoryIds.size()) {
            Set<Long> foundIds = categories.stream().map(Category::getId).collect(Collectors.toSet());
            List<Long> missingIds = categoryIds.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new EntityNotFoundException("Category(s) with ID(s) " + missingIds + " not found");
        }

        return categories;
    }

    private void createBookAuthorRelationships(Long bookId, List<Author> authors) {
        for (Author author : authors) {
            BookAuthor bookAuthor = new BookAuthor();
            bookAuthor.setBookId(bookId);
            bookAuthor.setAuthorId(author.getId());
            bookAuthorRepository.save(bookAuthor);
        }
    }

    private void createBookCategoryRelationships(Long bookId, List<Category> categories) {
        for (Category category : categories) {
            BookCategory bookCategory = new BookCategory();
            bookCategory.setBookId(bookId);
            bookCategory.setCategoryId(category.getId());
            bookCategoryRepository.save(bookCategory);
        }
    }

    private List<BookCopy> createBookCopies(Long bookId, List<CreateBookWithCopiesRequest.BookCopyRequest> copyRequests) {
        List<BookCopy> copies = new ArrayList<>();

        for (CreateBookWithCopiesRequest.BookCopyRequest copyRequest : copyRequests) {
            BookCopy copy = new BookCopy();
            copy.setBookId(bookId);
            copy.setCopyNumber(copyRequest.getCopyNumber());
            copy.setStatus(BookCopyStatus.AVAILABLE); // Default status for new copies
            copy.setCondition(mapConditionFromString(copyRequest.getCondition()));
            copy.setLocation(copyRequest.getLocation());
            // Save the copy first to get the ID for barcode generation
            copy = bookCopyRepository.save(copy);
            copies.add(copy);
        }

        return copies;
    }

    private BookCopyCondition mapConditionFromString(String condition) {
        return switch (condition.toLowerCase()) {
            case "excellent" -> BookCopyCondition.NEW;
            case "good" -> BookCopyCondition.GOOD;
            case "fair" -> BookCopyCondition.FAIR;
            case "poor" -> BookCopyCondition.POOR;
            case "damaged" -> BookCopyCondition.DAMAGED;
            default -> BookCopyCondition.GOOD; // Default to good condition
        };
    }

    private BookDetailResponse buildBookDetailResponse(Book book, Publisher publisher, List<Author> authors, List<Category> categories, List<BookCopy> copies) {
        BookDetailResponse response = new BookDetailResponse();
        response.setId(book.getPublicId());
        response.setTitle(book.getTitle());
        response.setAuthor(authors.stream().map(Author::getName).collect(Collectors.joining(", ")));
        response.setIsbn(book.getIsbn());
        response.setCategory(categories.stream().map(Category::getName).collect(Collectors.joining(", ")));
        response.setStatus(determineBookStatus(copies));
        response.setCopies(mapBookCopies(copies));
        response.setPublisher(Objects.nonNull(publisher) ? publisher.getName() : "-");
        response.setPublicationYear(book.getPublicationYear());
        response.setDescription(book.getDescription());
        response.setLanguage(book.getLanguage());
        response.setPages(book.getNumberOfPages());
        response.setCreatedAt(book.getCreatedAt());
        response.setUpdatedAt(book.getUpdatedAt());

        return response;
    }

    private void updateBookAuthorRelationships(Long bookId, List<Author> newAuthors) {
        // Delete existing relationships
        bookAuthorRepository.deleteByBookId(bookId);

        // Create new relationships
        createBookAuthorRelationships(bookId, newAuthors);
    }

    private void updateBookCategoryRelationships(Long bookId, List<Category> newCategories) {
        // Delete existing relationships
        bookCategoryRepository.deleteByBookId(bookId);

        // Create new relationships
        createBookCategoryRelationships(bookId, newCategories);
    }

    private List<BookCopy> updateBookCopies(Long bookId, List<UpdateBookWithCopiesRequest.BookCopyUpdateRequest> copyRequests) {
        // Get existing copies
        List<BookCopy> existingCopies = bookCopyRepository.findByBookIdAndDeletedAtIsNull(bookId);
        Map<UUID, BookCopy> existingCopiesMap = existingCopies.stream()
                .collect(Collectors.toMap(BookCopy::getPublicId, copy -> copy));

        List<BookCopy> updatedCopies = new ArrayList<>();

        for (UpdateBookWithCopiesRequest.BookCopyUpdateRequest copyRequest : copyRequests) {
            if (copyRequest.getDeleted() != null && copyRequest.getDeleted()) {
                // Mark copy for deletion (soft delete)
                if (copyRequest.getId() != null) {
                    BookCopy existingCopy = existingCopiesMap.get(copyRequest.getId());
                    if (existingCopy != null) {
                        existingCopy.markAsDeleted();
                        bookCopyRepository.save(existingCopy);
                    }
                }
                continue;
            }

            BookCopy copy;
            if (copyRequest.getId() != null && existingCopiesMap.containsKey(copyRequest.getId())) {
                // Update existing copy
                copy = existingCopiesMap.get(copyRequest.getId());
                copy.setCopyNumber(copyRequest.getCopyNumber());
                copy.setCondition(mapConditionFromString(copyRequest.getCondition()));
                copy.setLocation(copyRequest.getLocation());
            } else {
                // Create new copy
                copy = new BookCopy();
                copy.setBookId(bookId);
                copy.setCopyNumber(copyRequest.getCopyNumber());
                copy.setStatus(BookCopyStatus.AVAILABLE); // Default status for new copies
                copy.setCondition(mapConditionFromString(copyRequest.getCondition()));
                copy.setLocation(copyRequest.getLocation());
            }

            copy = bookCopyRepository.save(copy);
            updatedCopies.add(copy);
        }

        // Soft delete copies that are not in the request (removed copies)
        Set<UUID> requestCopyIds = copyRequests.stream()
                .map(UpdateBookWithCopiesRequest.BookCopyUpdateRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (BookCopy existingCopy : existingCopies) {
            if (!requestCopyIds.contains(existingCopy.getPublicId())) {
                existingCopy.markAsDeleted();
                bookCopyRepository.save(existingCopy);
            }
        }

        return updatedCopies;
    }
}
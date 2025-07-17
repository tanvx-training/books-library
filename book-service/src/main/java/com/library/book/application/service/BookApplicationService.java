package com.library.book.application.service;

import com.library.book.application.dto.request.BookCreateRequest;
import com.library.book.application.dto.request.PaginatedRequest;
import com.library.book.application.dto.response.AuthorResponse;
import com.library.book.application.dto.response.BookResponse;
import com.library.book.application.dto.response.CategoryResponse;
import com.library.book.application.dto.response.PaginatedResponse;
import com.library.book.application.dto.response.PublisherResponse;
import com.library.book.application.exception.BookApplicationException;
import com.library.book.application.exception.BookNotFoundException;
import com.library.book.domain.exception.BookDomainException;
import com.library.book.domain.exception.InvalidBookDataException;
import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.model.book.Book;
import com.library.book.domain.model.book.BookId;
import com.library.book.domain.model.book.BookTitle;
import com.library.book.domain.model.book.CoverImageUrl;
import com.library.book.domain.model.book.Description;
import com.library.book.domain.model.book.ISBN;
import com.library.book.domain.model.book.PublicationYear;
import com.library.book.domain.model.category.Category;
import com.library.book.domain.model.category.CategoryId;
import com.library.book.domain.model.publisher.Publisher;
import com.library.book.domain.model.publisher.PublisherId;
import com.library.book.domain.repository.AuthorRepository;
import com.library.book.domain.repository.BookRepository;
import com.library.book.domain.repository.CategoryRepository;
import com.library.book.domain.repository.PublisherRepository;
import com.library.book.domain.service.BookDomainService;
import com.library.book.infrastructure.config.security.JwtAuthenticationService;
import com.library.book.infrastructure.enums.LogLevel;
import com.library.book.infrastructure.enums.OperationType;
import com.library.book.infrastructure.logging.Loggable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookApplicationService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    private final BookDomainService bookDomainService;

    @Transactional(readOnly = true)
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.READ,
            resourceType = "Book",
            logReturnValue = false,
            performanceThresholdMs = 800L,
            messagePrefix = "BOOK_APP_SERVICE_LIST"
    )
    public PaginatedResponse<BookResponse> getAllBooks(PaginatedRequest paginatedRequest) {
        Page<BookResponse> bookResponses = bookRepository.findAll(paginatedRequest.getPage(),
                        paginatedRequest.getSize())
                .map(this::mapToBookResponse);
        return PaginatedResponse.from(bookResponses);
    }

    @Transactional
    @Loggable(
            level = LogLevel.ADVANCED,
            operationType = OperationType.CREATE,
            resourceType = "Book",
            performanceThresholdMs = 1500L,
            messagePrefix = "BOOK_APP_SERVICE_CREATE"
    )
    public BookResponse createBook(BookCreateRequest request, JwtAuthenticationService.AuthenticatedUser currentUser) {
        try {
            // Validate user permissions
            if (currentUser == null || !currentUser.canManageBooks()) {
                throw new BookApplicationException("User does not have permission to create books");
            }
            
            // Check if ISBN already exists
            if (bookRepository.existsByIsbn(request.getIsbn())) {
                throw new BookApplicationException("Book with ISBN " + request.getIsbn() + " already exists");
            }

            // Use factory for book creation
            com.library.book.domain.factory.BookFactory.BookCreationRequest factoryRequest = 
                com.library.book.domain.factory.BookFactory.BookCreationRequest.builder()
                    .title(request.getTitle())
                    .isbn(request.getIsbn())
                    .publisherId(request.getPublisherId())
                    .publicationYear(request.getPublicationYear())
                    .description(request.getDescription())
                    .coverImageUrl(request.getCoverImageUrl())
                    .authorIds(request.getAuthorIds())
                    .categoryIds(request.getCategoryIds())
                    .build();

            com.library.book.domain.factory.BookFactory bookFactory = 
                new com.library.book.domain.factory.BookFactory(authorRepository, categoryRepository, publisherRepository);
            Book book = bookFactory.createBook(factoryRequest);

            Book savedBook = bookRepository.save(book);
            
            log.info("Book created: {} by user: {}", savedBook.getTitle().getValue(), currentUser.getUsername());

            // Handle domain events if needed
            // eventPublisher.publish(savedBook.getDomainEvents());

            return mapToBookResponse(savedBook);
        } catch (InvalidBookDataException e) {
            log.error("Invalid book data: {}", e.getMessage());
            throw e;
        } catch (BookDomainException e) {
            log.error("Domain exception when creating book: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error when creating book", e);
            throw new BookApplicationException("Failed to create book", e);
        }
    }
    
    @Transactional(readOnly = true)
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.READ,
            resourceType = "Book",
            performanceThresholdMs = 500L,
            messagePrefix = "BOOK_APP_SERVICE_GET_BY_ID"
    )
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(new BookId(id))
                .orElseThrow(() -> new BookNotFoundException(id));

        return mapToBookResponse(book);
    }

    @Transactional(readOnly = true)
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.READ,
            resourceType = "Book",
            performanceThresholdMs = 800L,
            messagePrefix = "BOOK_APP_SERVICE_SEARCH"
    )
    public PaginatedResponse<BookResponse> searchBooks(String keyword, PaginatedRequest paginatedRequest) {
        Page<BookResponse> bookResponses = bookRepository.findAllByTitle(keyword, 
                paginatedRequest.getPage(), paginatedRequest.getSize())
                .map(this::mapToBookResponse);
        return PaginatedResponse.from(bookResponses);
    }

    @Transactional
    @Loggable(
            level = LogLevel.ADVANCED,
            operationType = OperationType.UPDATE,
            resourceType = "Book",
            performanceThresholdMs = 1500L,
            messagePrefix = "BOOK_APP_SERVICE_UPDATE"
    )
    public BookResponse updateBook(Long id, BookCreateRequest request, JwtAuthenticationService.AuthenticatedUser currentUser) {
        try {
            // Validate user permissions
            if (currentUser == null || !currentUser.canManageBooks()) {
                throw new BookApplicationException("User does not have permission to update books");
            }
            
            Book book = bookRepository.findById(new BookId(id))
                    .orElseThrow(() -> new BookNotFoundException(id));

            // Update book properties
            book.updateTitle(BookTitle.of(request.getTitle()));
            book.updateISBN(ISBN.of(request.getIsbn()));
            book.updatePublisher(new PublisherId(request.getPublisherId()));
            
            if (request.getPublicationYear() != null) {
                book.updatePublicationYear(PublicationYear.of(request.getPublicationYear()));
            } else {
                book.updatePublicationYear(PublicationYear.empty());
            }
            
            if (request.getDescription() != null) {
                book.updateDescription(Description.of(request.getDescription()));
            } else {
                book.updateDescription(Description.empty());
            }
            
            if (request.getCoverImageUrl() != null) {
                book.updateCoverImageUrl(CoverImageUrl.of(request.getCoverImageUrl()));
            } else {
                book.updateCoverImageUrl(CoverImageUrl.empty());
            }
            
            List<AuthorId> authorIds = request.getAuthorIds().stream()
                    .map(AuthorId::new)
                    .collect(Collectors.toList());
            book.updateAuthors(authorIds);
            
            List<CategoryId> categoryIds = request.getCategoryIds().stream()
                    .map(CategoryId::new)
                    .collect(Collectors.toList());
            book.updateCategories(categoryIds);

            Book savedBook = bookRepository.save(book);
            return mapToBookResponse(savedBook);
        } catch (InvalidBookDataException e) {
            log.error("Invalid book data: {}", e.getMessage());
            throw e;
        } catch (BookDomainException e) {
            log.error("Domain exception when updating book: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error when updating book", e);
            throw new BookApplicationException("Failed to update book", e);
        }
    }

    @Transactional
    @Loggable(
            level = LogLevel.ADVANCED,
            operationType = OperationType.DELETE,
            resourceType = "Book",
            performanceThresholdMs = 1000L,
            messagePrefix = "BOOK_APP_SERVICE_DELETE"
    )
    public void deleteBook(Long id, JwtAuthenticationService.AuthenticatedUser currentUser) {
        try {
            // Validate user permissions
            if (currentUser == null || !currentUser.canManageBooks()) {
                throw new BookApplicationException("User does not have permission to delete books");
            }
            
            Book book = bookRepository.findById(new BookId(id))
                    .orElseThrow(() -> new BookNotFoundException(id));
            
            // Check if book can be safely deleted using domain service
            if (!bookDomainService.canBookBeDeleted(new BookId(id))) {
                throw new BookApplicationException("Cannot delete book with active borrowings");
            }
            
            book.markAsDeleted();
            bookRepository.save(book);
            
            log.info("Book deleted: {} by user: {}", book.getTitle().getValue(), currentUser.getUsername());
        } catch (BookApplicationException e) {
            log.error("Book application exception: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error deleting book with id {}", id, e);
            throw new BookApplicationException("Failed to delete book", e);
        }
    }

    private BookResponse mapToBookResponse(Book book) {
        PublisherResponse publisherResponse = null;
        if (book.getPublisherId() != null) {
            Optional<Publisher> publisher = publisherRepository.findById(book.getPublisherId());
            if (publisher.isPresent()) {
                publisherResponse = PublisherResponse.builder()
                        .id(publisher.get().getId().getValue())
                        .name(publisher.get().getName().getValue())
                        .address(publisher.get().getAddress().getValue())
                        .build();
            }
        }

        List<AuthorResponse> authorResponses = book.getAuthorIds().stream()
                .map(authorId -> {
                    Optional<Author> author = authorRepository.findById(authorId);
                    return author.map(value -> AuthorResponse.builder()
                            .id(value.getId().getValue())
                            .name(value.getName().getValue())
                            .biography(value.getBiography().getValue())
                            .build()).orElse(null);
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        List<CategoryResponse> categoryResponses = book.getCategoryIds().stream()
                .map(categoryId -> {
                    Optional<Category> category = categoryRepository.findById(categoryId);
                    return category.map(value -> CategoryResponse.builder()
                            .id(value.getId().getValue())
                            .name(value.getName().getValue())
                            .description(value.getDescription().getValue())
                            .build()).orElse(null);
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        return BookResponse.builder()
                .id(book.getId().getValue())
                .title(book.getTitle().getValue())
                .isbn(book.getIsbn().getValue())
                .publicationYear(book.getPublicationYear().getValue())
                .description(book.getDescription().getValue())
                .coverImageUrl(book.getCoverImageUrl().getValue())
                .publisher(publisherResponse)
                .authors(authorResponses)
                .categories(categoryResponses)
                .build();
    }
} 
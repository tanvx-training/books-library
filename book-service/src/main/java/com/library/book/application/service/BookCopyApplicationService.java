package com.library.book.application.service;

import com.library.book.application.dto.request.BookCopyCreateRequest;
import com.library.book.application.dto.request.PaginatedRequest;
import com.library.book.application.dto.response.BookCopyResponse;
import com.library.book.application.dto.response.PaginatedResponse;
import com.library.book.application.exception.BookCopyNotFoundException;
import com.library.book.application.service.UserContextService.UserContext;
import com.library.book.domain.factory.BookCopyFactory;
import com.library.book.domain.model.book.BookId;
import com.library.book.domain.model.bookcopy.BookCopy;
import com.library.book.domain.model.bookcopy.BookCopyId;
import com.library.book.domain.repository.BookCopyRepository;
import com.library.book.domain.service.BookCopyDomainService;
import com.library.book.infrastructure.enums.LogLevel;
import com.library.book.infrastructure.enums.OperationType;
import com.library.book.infrastructure.logging.Loggable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for BookCopy operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookCopyApplicationService {
    
    private final BookCopyRepository bookCopyRepository;
    private final BookCopyFactory bookCopyFactory;
    private final BookCopyDomainService bookCopyDomainService;
    
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "BookCopy",
        performanceThresholdMs = 1000L,
        messagePrefix = "BOOK_COPY_CREATE"
    )
    public BookCopyResponse createBookCopy(BookCopyCreateRequest request, UserContext userContext) {
        validateUserCanManageBooks(userContext);
        
        BookCopyFactory.BookCopyCreationRequest factoryRequest = BookCopyFactory.BookCopyCreationRequest.builder()
            .bookId(request.getBookId())
            .copyNumber(request.getCopyNumber())
            .condition(request.getCondition())
            .location(request.getLocation())
            .build();
        
        BookCopy bookCopy = bookCopyFactory.createBookCopy(factoryRequest);
        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);
        
        log.info("Book copy created: {} by user: {}", savedBookCopy.getId(), userContext.getUsername());
        
        return mapToBookCopyResponse(savedBookCopy);
    }
    
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "BookCopy",
        performanceThresholdMs = 2000L,
        messagePrefix = "BOOK_COPY_CREATE_MULTIPLE"
    )
    public List<BookCopyResponse> createMultipleBookCopies(
            Long bookId, 
            int numberOfCopies, 
            String locationPrefix,
            UserContext userContext) {
        
        validateUserCanManageBooks(userContext);
        
        List<BookCopy> bookCopies = bookCopyFactory.createMultipleCopies(
            bookId, numberOfCopies, null, locationPrefix);
        
        List<BookCopy> savedCopies = bookCopies.stream()
            .map(bookCopyRepository::save)
            .collect(Collectors.toList());
        
        log.info("Created {} book copies for book {} by user: {}", 
            numberOfCopies, bookId, userContext.getUsername());
        
        return savedCopies.stream()
            .map(this::mapToBookCopyResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "BookCopy",
        performanceThresholdMs = 1000L,
        messagePrefix = "BOOK_COPY_BORROW"
    )
    public void borrowBookCopy(Long bookCopyId, UserContext userContext, int loanPeriodDays) {
        validateUserCanBorrowBooks(userContext);
        
        // Check if user has overdue books
        if (bookCopyDomainService.hasOverdueBooks(userContext.getKeycloakId())) {
            throw new IllegalStateException("Cannot borrow books while having overdue items");
        }
        
        bookCopyDomainService.borrowBookCopy(
            new BookCopyId(bookCopyId), 
            userContext.getKeycloakId(), 
            loanPeriodDays);
        
        log.info("Book copy {} borrowed by user: {}", bookCopyId, userContext.getUsername());
    }
    
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "BookCopy",
        performanceThresholdMs = 1000L,
        messagePrefix = "BOOK_COPY_RETURN"
    )
    public void returnBookCopy(Long bookCopyId, UserContext userContext) {
        BookCopy bookCopy = bookCopyRepository.findById(new BookCopyId(bookCopyId))
            .orElseThrow(() -> new BookCopyNotFoundException(bookCopyId));
        
        // Validate user can return this book (either the borrower or librarian/admin)
        if (!bookCopy.isBorrowedBy(userContext.getKeycloakId()) && !userContext.canManageBooks()) {
            throw new IllegalStateException("You can only return books you have borrowed");
        }
        
        bookCopyDomainService.returnBookCopy(new BookCopyId(bookCopyId));
        
        log.info("Book copy {} returned by user: {}", bookCopyId, userContext.getUsername());
    }
    
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "BookCopy",
        performanceThresholdMs = 1000L,
        messagePrefix = "BOOK_COPY_RESERVE"
    )
    public void reserveBookCopy(Long bookCopyId, UserContext userContext) {
        validateUserCanBorrowBooks(userContext);
        
        bookCopyDomainService.reserveBookCopy(
            new BookCopyId(bookCopyId), 
            userContext.getKeycloakId());
        
        log.info("Book copy {} reserved by user: {}", bookCopyId, userContext.getUsername());
    }
    
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "BookCopy",
        performanceThresholdMs = 500L,
        messagePrefix = "BOOK_COPY_GET_BY_ID"
    )
    public BookCopyResponse getBookCopyById(Long id) {
        BookCopy bookCopy = bookCopyRepository.findById(new BookCopyId(id))
            .orElseThrow(() -> new BookCopyNotFoundException(id));
        
        return mapToBookCopyResponse(bookCopy);
    }
    
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "BookCopy",
        performanceThresholdMs = 800L,
        messagePrefix = "BOOK_COPY_GET_BY_BOOK"
    )
    public List<BookCopyResponse> getBookCopiesByBookId(Long bookId) {
        List<BookCopy> bookCopies = bookCopyRepository.findByBookId(new BookId(bookId));
        
        return bookCopies.stream()
            .map(this::mapToBookCopyResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "BookCopy",
        performanceThresholdMs = 800L,
        messagePrefix = "BOOK_COPY_GET_USER_BORROWED"
    )
    public List<BookCopyResponse> getUserBorrowedBooks(UserContext userContext) {
        List<BookCopy> borrowedCopies = bookCopyDomainService.getUserBorrowedCopies(userContext.getKeycloakId());
        
        return borrowedCopies.stream()
            .map(this::mapToBookCopyResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "BookCopy",
        performanceThresholdMs = 800L,
        messagePrefix = "BOOK_COPY_GET_ALL"
    )
    public PaginatedResponse<BookCopyResponse> getAllBookCopies(PaginatedRequest paginatedRequest) {
        Page<BookCopyResponse> bookCopyResponses = bookCopyRepository.findAll(
            paginatedRequest.getPage(), paginatedRequest.getSize())
            .map(this::mapToBookCopyResponse);
        
        return PaginatedResponse.from(bookCopyResponses);
    }
    
    @Transactional(readOnly = true)
    public BookCopyDomainService.BookCopyStatistics getBookCopyStatistics(Long bookId) {
        return bookCopyDomainService.getBookCopyStatistics(new BookId(bookId));
    }
    
    private void validateUserCanManageBooks(UserContext userContext) {
        if (userContext == null || !userContext.canManageBooks()) {
            throw new IllegalStateException("User does not have permission to manage books");
        }
    }
    
    private void validateUserCanBorrowBooks(UserContext userContext) {
        if (userContext == null || !userContext.canBorrowBooks()) {
            throw new IllegalStateException("User does not have permission to borrow books");
        }
    }
    
    private BookCopyResponse mapToBookCopyResponse(BookCopy bookCopy) {
        return BookCopyResponse.builder()
            .id(bookCopy.getId().getValue())
            .bookId(bookCopy.getBookId().getValue())
            .copyNumber(bookCopy.getCopyNumber().getValue())
            .status(bookCopy.getStatus())
            .condition(bookCopy.getCondition())
            .location(bookCopy.getLocation().getValue())
            .acquiredDate(bookCopy.getAcquiredDate())
            .currentBorrowerKeycloakId(bookCopy.getCurrentBorrowerKeycloakId())
            .borrowedDate(bookCopy.getBorrowedDate())
            .dueDate(bookCopy.getDueDate())
            .isOverdue(bookCopy.isOverdue())
            .build();
    }
}
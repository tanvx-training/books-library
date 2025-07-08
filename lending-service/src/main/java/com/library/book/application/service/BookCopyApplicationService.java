package com.library.book.application.service;

import com.library.book.application.dto.request.BookCopyCreateRequest;
import com.library.book.application.dto.request.BookCopyUpdateRequest;
import com.library.book.application.dto.response.BookCopyResponse;
import com.library.book.application.exception.BookCopyApplicationException;
import com.library.book.application.mapper.BookCopyDtoMapper;
import com.library.book.domain.exception.BookCopyNotFoundException;
import com.library.book.domain.exception.InvalidBookCopyDataException;
import com.library.book.domain.model.bookcopy.BookCopy;
import com.library.book.domain.service.BookCopyDomainService;
import com.library.common.aop.annotation.Loggable;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookCopyApplicationService {
    
    private final BookCopyDomainService bookCopyDomainService;
    private final BookCopyDtoMapper bookCopyDtoMapper;
    
    /**
     * Get all copies of a specific book
     * 
     * @param bookId the ID of the book
     * @return list of book copies
     */
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = false,
        logExecutionTime = true,
        performanceThresholdMs = 800L,
        messagePrefix = "BOOK_COPY_APP_SERVICE_BY_BOOK",
        customTags = {
            "layer=application", 
            "transaction=readonly", 
            "inventory_check=true"
        }
    )
    public List<BookCopyResponse> getBookCopiesByBookId(Long bookId) {
        try {
            return bookCopyDomainService.findBookCopiesByBookId(bookId).stream()
                    .map(bookCopyDtoMapper::toResponseDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new BookCopyApplicationException("Failed to get book copies for book ID: " + bookId, e);
        }
    }
    
    /**
     * Add a new copy to a book
     *
     * @param request the book copy data
     * @return the created book copy
     */
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "BOOK_COPY_APP_SERVICE_CREATE",
        customTags = {
            "layer=application", 
            "transaction=write", 
            "inventory_management=true"
        }
    )
    public BookCopyResponse addBookCopy(BookCopyCreateRequest request) {
        try {
            BookCopy bookCopy = bookCopyDomainService.createBookCopy(
                    request.getBookId(),
                    request.getBookTitle(),
                    request.getCopyNumber(),
                    request.getStatus(),
                    request.getCondition(),
                    request.getLocation()
            );
            
            return bookCopyDtoMapper.toResponseDto(bookCopy);
        } catch (InvalidBookCopyDataException e) {
            throw new BookCopyApplicationException("Invalid book copy data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new BookCopyApplicationException("Failed to create book copy: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update a book copy
     * 
     * @param bookCopyId the ID of the book copy
     * @param request the updated book copy data
     * @return the updated book copy
     */
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "BOOK_COPY_APP_SERVICE_UPDATE",
        customTags = {
            "layer=application", 
            "transaction=write", 
            "inventory_management=true"
        }
    )
    public BookCopyResponse updateBookCopy(Long bookCopyId, BookCopyUpdateRequest request) {
        try {
            BookCopy bookCopy = bookCopyDomainService.updateBookCopy(
                    bookCopyId,
                    request.getCopyNumber(),
                    request.getCondition(),
                    request.getLocation(),
                    request.getStatus()
            );
            
            return bookCopyDtoMapper.toResponseDto(bookCopy);
        } catch (BookCopyNotFoundException e) {
            throw new BookCopyApplicationException("Book copy not found with ID: " + bookCopyId, e);
        } catch (InvalidBookCopyDataException e) {
            throw new BookCopyApplicationException("Invalid book copy data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new BookCopyApplicationException("Failed to update book copy: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update the status of a book copy
     * 
     * @param bookCopyId the ID of the book copy
     * @param status the new status
     * @return the updated book copy
     */
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 1500L,
        messagePrefix = "BOOK_COPY_APP_SERVICE_STATUS_UPDATE",
        customTags = {
            "layer=application", 
            "transaction=write", 
            "status_change=true",
            "inventory_management=true"
        }
    )
    public BookCopyResponse updateBookCopyStatus(Long bookCopyId, String status) {
        try {
            BookCopy bookCopy = bookCopyDomainService.updateBookCopyStatus(bookCopyId, status);
            return bookCopyDtoMapper.toResponseDto(bookCopy);
        } catch (BookCopyNotFoundException e) {
            throw new BookCopyApplicationException("Book copy not found with ID: " + bookCopyId, e);
        } catch (InvalidBookCopyDataException e) {
            throw new BookCopyApplicationException("Invalid status: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new BookCopyApplicationException("Failed to update book copy status: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete a book copy
     * 
     * @param bookCopyId the ID of the book copy
     * @return true if deleted successfully
     */
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.DELETE,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "BOOK_COPY_APP_SERVICE_DELETE",
        customTags = {
            "layer=application", 
            "transaction=write", 
            "data_removal=true",
            "inventory_management=true"
        }
    )
    public boolean deleteBookCopy(Long bookCopyId) {
        try {
            return bookCopyDomainService.deleteBookCopy(bookCopyId);
        } catch (BookCopyNotFoundException e) {
            throw new BookCopyApplicationException("Book copy not found with ID: " + bookCopyId, e);
        } catch (InvalidBookCopyDataException e) {
            throw new BookCopyApplicationException("Cannot delete book copy: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new BookCopyApplicationException("Failed to delete book copy: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get a book copy by ID
     * 
     * @param bookCopyId the ID of the book copy
     * @return the book copy
     */
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.READ,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 300L,
        messagePrefix = "BOOK_COPY_APP_SERVICE_DETAIL",
        customTags = {
            "layer=application", 
            "transaction=readonly", 
            "single_entity=true",
            "inventory_check=true"
        }
    )
    public BookCopyResponse getBookCopyById(Long bookCopyId) {
        try {
            BookCopy bookCopy = bookCopyDomainService.getBookCopyById(bookCopyId);
            return bookCopyDtoMapper.toResponseDto(bookCopy);
        } catch (BookCopyNotFoundException e) {
            throw new BookCopyApplicationException("Book copy not found with ID: " + bookCopyId, e);
        } catch (Exception e) {
            throw new BookCopyApplicationException("Failed to get book copy: " + e.getMessage(), e);
        }
    }
} 
package com.library.book.service.impl;

import com.library.book.utils.enums.BookCopyStatus;
import com.library.book.model.Book;
import com.library.book.model.BookCopy;
import com.library.book.service.BookCopyService;
import com.library.book.repository.BookCopyRepository;
import com.library.book.repository.BookRepository;
import com.library.book.dto.request.BookCopyRequestDTO;
import com.library.book.dto.request.BookCopyUpdateDTO;
import com.library.book.dto.response.BookCopyResponseDTO;
import com.library.book.utils.mapper.BookCopyMapper;
import com.library.common.aop.annotation.Loggable;
import com.library.common.aop.exception.BadRequestException;
import com.library.common.aop.exception.ResourceExistedException;
import com.library.common.aop.exception.ResourceNotFoundException;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookCopyServiceImpl implements BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;
    private final BookCopyMapper bookCopyMapper;

    @Override
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = false, // Don't log collections in service layer
        logExecutionTime = true,
        performanceThresholdMs = 800L,
        messagePrefix = "BOOK_COPY_SERVICE_BY_BOOK",
        customTags = {
            "layer=service", 
            "transaction=readonly", 
            "relationship_query=true",
            "inventory_check=true",
            "collection_mapping=true"
        }
    )
    public List<BookCopyResponseDTO> getBookCopiesByBookId(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book", "id", bookId);
        }
        
        return bookCopyRepository.findByBookId(bookId).stream()
                .map(bookCopyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
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
        messagePrefix = "BOOK_COPY_SERVICE_CREATE",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "business_validation=true",
            "uniqueness_check=true",
            "status_validation=true",
            "inventory_management=true",
            "relationship_setup=true"
        }
    )
    public BookCopyResponseDTO addBookCopy(BookCopyRequestDTO bookCopyRequestDTO) {
        Book book = bookRepository.findById(bookCopyRequestDTO.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookCopyRequestDTO.getBookId()));
        
        // Check if copy number already exists for this book
        if (bookCopyRepository.existsByBookIdAndCopyNumber(bookCopyRequestDTO.getBookId(), bookCopyRequestDTO.getCopyNumber())) {
            throw new ResourceExistedException("BookCopy", "copyNumber", bookCopyRequestDTO.getCopyNumber());
        }
        
        BookCopy bookCopy = bookCopyMapper.toEntity(bookCopyRequestDTO);
        bookCopy.setBook(book);
        
        // Set default status to AVAILABLE if not provided
        if (bookCopyRequestDTO.getStatus() == null || bookCopyRequestDTO.getStatus().isEmpty()) {
            bookCopy.setStatus(BookCopyStatus.AVAILABLE.name());
        } else {
            validateStatus(bookCopyRequestDTO.getStatus());
            bookCopy.setStatus(bookCopyRequestDTO.getStatus());
        }
        
        bookCopyRepository.save(bookCopy);
        return bookCopyMapper.toDto(bookCopy);
    }

    @Override
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
        messagePrefix = "BOOK_COPY_SERVICE_UPDATE",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "business_validation=true",
            "status_validation=true",
            "conflict_check=true",
            "inventory_management=true"
        }
    )
    public BookCopyResponseDTO updateBookCopy(Long bookCopyId, BookCopyUpdateDTO bookCopyRequestDTO) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy", "id", bookCopyId));
        
        // Check if the new copy number conflicts with an existing one (if changed)
        if (!bookCopy.getCopyNumber().equals(bookCopyRequestDTO.getCopyNumber()) &&
                bookCopyRepository.existsByBookIdAndCopyNumber(
                        bookCopy.getBook().getId(), bookCopyRequestDTO.getCopyNumber())) {
            throw new ResourceExistedException("BookCopy", "copyNumber", bookCopyRequestDTO.getCopyNumber());
        }
        
        bookCopy.setCopyNumber(bookCopyRequestDTO.getCopyNumber());
        bookCopy.setCondition(bookCopyRequestDTO.getCondition());
        bookCopy.setLocation(bookCopyRequestDTO.getLocation());
        
        // Only update status if provided and if allowed
        if (bookCopyRequestDTO.getStatus() != null && !bookCopyRequestDTO.getStatus().isEmpty()) {
            validateStatus(bookCopyRequestDTO.getStatus());
            validateStatusChange(bookCopy.getStatus(), bookCopyRequestDTO.getStatus(), bookCopyId);
            bookCopy.setStatus(bookCopyRequestDTO.getStatus());
        }
        
        bookCopyRepository.save(bookCopy);
        return bookCopyMapper.toDto(bookCopy);
    }

    @Override
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
        messagePrefix = "BOOK_COPY_SERVICE_STATUS_UPDATE",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "business_critical=true",
            "status_change=true",
            "inventory_management=true",
            "availability_tracking=true",
            "business_rules_validation=true"
        }
    )
    public BookCopyResponseDTO updateBookCopyStatus(Long bookCopyId, String status) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy", "id", bookCopyId));
        
        validateStatus(status);
        validateStatusChange(bookCopy.getStatus(), status, bookCopyId);
        
        bookCopy.setStatus(status);
        bookCopyRepository.save(bookCopy);
        
        return bookCopyMapper.toDto(bookCopy);
    }

    @Override
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
        messagePrefix = "BOOK_COPY_SERVICE_DELETE",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "business_critical=true",
            "data_removal=true",
            "inventory_management=true",
            "safety_checks=true",
            "borrowing_validation=true",
            "audit_required=true"
        }
    )
    public boolean deleteBookCopy(Long bookCopyId) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy", "id", bookCopyId));
        
        // Check if the book copy is available and not borrowed
        if (!BookCopyStatus.AVAILABLE.name().equals(bookCopy.getStatus())) {
            throw new BadRequestException("Không thể xóa bản sao sách đang không ở trạng thái 'Có sẵn'");
        }
        
        // Check if there are any active borrowings
        if (bookCopyRepository.countByIdAndBorrowingsReturnDateIsNull(bookCopyId) > 0) {
            throw new BadRequestException("Không thể xóa bản sao sách đang được mượn");
        }
        
        bookCopyRepository.delete(bookCopy);
        return true;
    }

    @Override
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
        messagePrefix = "BOOK_COPY_SERVICE_DETAIL",
        customTags = {
            "layer=service", 
            "transaction=readonly", 
            "single_entity=true",
            "inventory_check=true",
            "entity_mapping=true"
        }
    )
    public BookCopyResponseDTO getBookCopyById(Long bookCopyId) {
        return bookCopyRepository.findById(bookCopyId)
                .map(bookCopyMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy", "id", bookCopyId));
    }
    
    /**
     * Validate if the status is valid
     * 
     * @param status the status to validate
     */
    private void validateStatus(String status) {
        try {
            BookCopyStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Trạng thái không hợp lệ: " + status);
        }
    }
    
    /**
     * Validate if the status change is allowed
     * 
     * @param currentStatus the current status
     * @param newStatus the new status
     * @param bookCopyId the book copy ID
     */
    private void validateStatusChange(String currentStatus, String newStatus, Long bookCopyId) {
        // Prevent manual change to BORROWED or RESERVED status
        if ((BookCopyStatus.BORROWED.name().equals(newStatus) || 
             BookCopyStatus.RESERVED.name().equals(newStatus)) && 
            !currentStatus.equals(newStatus)) {
            throw new BadRequestException("Không thể thay đổi trạng thái thành " + newStatus + " thủ công");
        }
        
        // Check if book is currently borrowed when changing status
        if (!BookCopyStatus.BORROWED.name().equals(newStatus) && 
            BookCopyStatus.BORROWED.name().equals(currentStatus) && 
            bookCopyRepository.countByIdAndBorrowingsReturnDateIsNull(bookCopyId) > 0) {
            throw new BadRequestException("Không thể thay đổi trạng thái của sách đang được mượn");
        }
    }
} 
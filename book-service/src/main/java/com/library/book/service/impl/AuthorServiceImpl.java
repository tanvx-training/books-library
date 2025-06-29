package com.library.book.service.impl;

import com.library.book.model.Author;
import com.library.book.service.AuthorService;
import com.library.book.repository.AuthorRepository;
import com.library.book.dto.request.AuthorCreateDTO;
import com.library.book.dto.response.AuthorResponseDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.book.utils.mapper.AuthorMapper;
import com.library.book.utils.mapper.BookMapper;
import com.library.common.aop.annotation.Loggable;
import com.library.common.dto.PaginatedRequest;
import com.library.common.aop.exception.ResourceNotFoundException;
import com.library.common.dto.PaginatedResponse;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorMapper authorMapper;

    private final BookMapper bookMapper;

    private final AuthorRepository authorRepository;

    @Override
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Author",
        logArguments = true,
        logReturnValue = false, // Don't log collections in service layer
        logExecutionTime = true,
        performanceThresholdMs = 800L,
        messagePrefix = "AUTHOR_SERVICE_LIST",
        customTags = {"layer=service", "transaction=readonly", "soft_delete_filter=true", "pagination=true"}
    )
    public PaginatedResponse<AuthorResponseDTO> getAllAuthors(PaginatedRequest paginatedRequest) {
        Pageable pageable = paginatedRequest.toPageable();
        Page<AuthorResponseDTO> page = authorRepository.findAllByDeleteFlg(Boolean.FALSE, pageable)
                .map(authorMapper::toDto);
        return PaginatedResponse.from(page);
    }

    @Override
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "Author",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 1500L,
        messagePrefix = "AUTHOR_SERVICE_CREATE",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "entity_mapping=true",
            "catalog_management=true"
        }
    )
    public AuthorResponseDTO createAuthor(AuthorCreateDTO authorCreateDTO) {
        Author author = authorMapper.toEntity(authorCreateDTO);
        authorRepository.save(author);
        return authorMapper.toDto(author);
    }

    @Override
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Author",
        logArguments = true,
        logReturnValue = false, // Don't log book lists - can be large
        logExecutionTime = true,
        performanceThresholdMs = 1000L,
        messagePrefix = "AUTHOR_SERVICE_BOOKS",
        customTags = {
            "layer=service", 
            "transaction=readonly", 
            "relationship_query=true",
            "lazy_loading=true",
            "collection_mapping=true"
        }
    )
    public List<BookResponseDTO> getBooksByAuthor(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", authorId));
        return author.getBooks()
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }
}

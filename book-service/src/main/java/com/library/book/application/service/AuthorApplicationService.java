package com.library.book.application.service;

import com.library.book.application.dto.request.AuthorCreateRequest;
import com.library.book.application.dto.request.PaginatedRequest;
import com.library.book.application.dto.response.AuthorResponse;
import com.library.book.application.dto.response.PaginatedResponse;
import com.library.book.application.exception.AuthorApplicationException;
import com.library.book.application.exception.AuthorNotFoundException;
import com.library.book.domain.exception.AuthorDomainException;
import com.library.book.domain.exception.InvalidAuthorDataException;
import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.repository.AuthorRepository;
import com.library.book.domain.service.AuthorDomainService;
import com.library.book.infrastructure.enums.LogLevel;
import com.library.book.infrastructure.enums.OperationType;
import com.library.book.infrastructure.logging.Loggable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorApplicationService {

    private final AuthorRepository authorRepository;
    private final AuthorDomainService authorDomainService;

    @Transactional(readOnly = true)
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.READ,
            resourceType = "Author",
            logReturnValue = false,
            performanceThresholdMs = 800L,
            messagePrefix = "AUTHOR_APP_SERVICE_LIST"
    )
    public PaginatedResponse<AuthorResponse> getAllAuthors(PaginatedRequest paginatedRequest) {
        Page<AuthorResponse> authorResponses = authorRepository.findAll(paginatedRequest.getPage(),
                        paginatedRequest.getSize())
                .map(this::mapToAuthorResponse);
        return PaginatedResponse.from(authorResponses);
    }

    @Transactional
    @Loggable(
            level = LogLevel.ADVANCED,
            operationType = OperationType.CREATE,
            resourceType = "Author",
            performanceThresholdMs = 1500L,
            messagePrefix = "AUTHOR_APP_SERVICE_CREATE"
    )
    public AuthorResponse createAuthor(AuthorCreateRequest request) {
        try {
            Author author = authorDomainService.createNewAuthor(
                    request.getName(),
                    request.getBiography()
            );

            Author savedAuthor = authorRepository.save(author);

            // Xử lý domain events nếu cần
            // eventPublisher.publish(savedAuthor.getDomainEvents());

            return mapToAuthorResponse(savedAuthor);
        } catch (InvalidAuthorDataException e) {
            log.error("Invalid author data: {}", e.getMessage());
            throw e; // Rethrow để được xử lý bởi exception handler
        } catch (AuthorDomainException e) {
            log.error("Domain exception when creating author: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error when creating author", e);
            throw new AuthorApplicationException("Failed to create author", e);
        }
    }

    @Transactional(readOnly = true)
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.READ,
            resourceType = "Author",
            performanceThresholdMs = 500L,
            messagePrefix = "AUTHOR_APP_SERVICE_GET_BY_ID"
    )
    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(new AuthorId(id))
                .orElseThrow(() -> new AuthorNotFoundException(id));

        return mapToAuthorResponse(author);
    }

    private AuthorResponse mapToAuthorResponse(Author author) {
        return AuthorResponse.builder()
                .id(author.getId().getValue())
                .name(author.getName().getValue())
                .biography(author.getBiography().getValue())
                .build();
    }
}
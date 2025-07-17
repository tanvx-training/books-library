package com.library.book.application.service;

import com.library.book.application.dto.request.AuthorCreateRequest;
import com.library.book.application.dto.request.PaginatedRequest;
import com.library.book.application.dto.response.AuthorResponse;
import com.library.book.application.dto.response.PaginatedResponse;
import com.library.book.application.exception.AuthorApplicationException;
import com.library.book.application.exception.AuthorNotFoundException;
import com.library.book.domain.exception.AuthorDomainException;
import com.library.book.domain.exception.InvalidAuthorDataException;
import com.library.book.domain.factory.AuthorFactory;
import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.repository.AuthorRepository;
import com.library.book.domain.service.AuthorDomainService;
import com.library.book.infrastructure.config.security.JwtAuthenticationService;
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
        Page<AuthorResponse> authorResponses = authorRepository.findAll(
                paginatedRequest.toPageable())
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
    public AuthorResponse createAuthor(AuthorCreateRequest request, JwtAuthenticationService.AuthenticatedUser currentUser) {
        try {
            // Validate user permissions
            if (currentUser == null || !currentUser.canManageBooks()) {
                throw new AuthorApplicationException("User does not have permission to create authors");
            }
            
            // Use factory for author creation
            AuthorFactory.AuthorCreationRequest factoryRequest = AuthorFactory.AuthorCreationRequest.builder()
                .name(request.getName())
                .biography(request.getBiography())
                .createdByKeycloakId(currentUser.getKeycloakId())
                .build();
            
            AuthorFactory authorFactory = new AuthorFactory(authorRepository);
            Author author = authorFactory.createAuthor(factoryRequest);

            Author savedAuthor = authorRepository.save(author);
            
            log.info("Author created: {} by user: {}", savedAuthor.getName().getValue(), currentUser.getUsername());

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

    @Transactional
    @Loggable(
            level = LogLevel.ADVANCED,
            operationType = OperationType.UPDATE,
            resourceType = "Author",
            performanceThresholdMs = 1000L,
            messagePrefix = "AUTHOR_APP_SERVICE_UPDATE"
    )
    public AuthorResponse updateAuthor(Long id, AuthorCreateRequest request, JwtAuthenticationService.AuthenticatedUser currentUser) {
        try {
            // Validate user permissions
            if (currentUser == null || !currentUser.canManageBooks()) {
                throw new AuthorApplicationException("User does not have permission to update authors");
            }
            
            Author author = authorRepository.findById(new AuthorId(id))
                    .orElseThrow(() -> new AuthorNotFoundException(id));
            
            // Update author information
            author.updateName(com.library.book.domain.model.author.AuthorName.of(request.getName()), 
                currentUser.getKeycloakId());
            author.updateBiography(com.library.book.domain.model.author.Biography.of(request.getBiography()), 
                currentUser.getKeycloakId());
            
            Author savedAuthor = authorRepository.save(author);
            
            log.info("Author updated: {} by user: {}", savedAuthor.getName().getValue(), currentUser.getUsername());
            
            return mapToAuthorResponse(savedAuthor);
        } catch (InvalidAuthorDataException e) {
            log.error("Invalid author data: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error when updating author", e);
            throw new AuthorApplicationException("Failed to update author", e);
        }
    }

    @Transactional
    @Loggable(
            level = LogLevel.ADVANCED,
            operationType = OperationType.DELETE,
            resourceType = "Author",
            performanceThresholdMs = 1000L,
            messagePrefix = "AUTHOR_APP_SERVICE_DELETE"
    )
    public void deleteAuthor(Long id, JwtAuthenticationService.AuthenticatedUser currentUser) {
        try {
            // Validate user permissions
            if (currentUser == null || !currentUser.canManageBooks()) {
                throw new AuthorApplicationException("User does not have permission to delete authors");
            }
            
            Author author = authorRepository.findById(new AuthorId(id))
                    .orElseThrow(() -> new AuthorNotFoundException(id));
            
            // Check if author can be deleted using domain service
            if (!authorDomainService.canAuthorBeDeleted(new AuthorId(id))) {
                throw new AuthorApplicationException("Cannot delete author with associated books");
            }
            
            author.markAsDeleted(currentUser.getKeycloakId());
            authorRepository.save(author);
            
            log.info("Author deleted: {} by user: {}", author.getName().getValue(), currentUser.getUsername());
        } catch (Exception e) {
            log.error("Error deleting author with id {}", id, e);
            throw new AuthorApplicationException("Failed to delete author", e);
        }
    }

    @Transactional(readOnly = true)
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.READ,
            resourceType = "Author",
            performanceThresholdMs = 800L,
            messagePrefix = "AUTHOR_APP_SERVICE_GET_PROLIFIC"
    )
    public java.util.List<AuthorResponse> getProlificAuthors(int minimumBookCount) {
        java.util.List<Author> prolificAuthors = authorDomainService.getProlificAuthors(minimumBookCount);
        
        return prolificAuthors.stream()
            .map(this::mapToAuthorResponse)
            .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.READ,
            resourceType = "Author",
            performanceThresholdMs = 500L,
            messagePrefix = "AUTHOR_APP_SERVICE_GET_STATISTICS"
    )
    public AuthorDomainService.AuthorStatistics getAuthorStatistics(Long id) {
        return authorDomainService.getAuthorStatistics(new AuthorId(id));
    }

    private AuthorResponse mapToAuthorResponse(Author author) {
        return AuthorResponse.builder()
                .id(author.getId().getValue())
                .name(author.getName().getValue())
                .biography(author.getBiography().getValue())
                .bookCount(author.getBookCount())
                .canBeDeleted(author.canBeDeleted())
                .createdAt(author.getCreatedAt())
                .updatedAt(author.getUpdatedAt())
                .build();
    }
}
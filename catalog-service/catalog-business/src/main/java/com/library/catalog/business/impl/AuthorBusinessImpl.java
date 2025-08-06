package com.library.catalog.business.impl;

import com.library.catalog.business.aop.exception.EntityNotFoundException;
import com.library.catalog.business.dto.request.AuthorSearchRequest;
import com.library.catalog.business.security.UnifiedAuthenticationService;
import com.library.catalog.repository.AuthorRepository;
import com.library.catalog.repository.entity.Author;
import com.library.catalog.business.AuthorBusiness;
import com.library.catalog.business.dto.request.CreateAuthorRequest;
import com.library.catalog.business.dto.request.UpdateAuthorRequest;
import com.library.catalog.business.dto.response.AuthorResponse;
import com.library.catalog.business.dto.response.PagedAuthorResponse;
import com.library.catalog.business.aop.exception.EntityValidationException;
import com.library.catalog.business.mapper.AuthorMapper;
import com.library.catalog.business.kafka.publisher.AuditService;
import com.library.catalog.business.util.EntityExceptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthorBusinessImpl implements AuthorBusiness {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final AuditService auditService;
    private final UnifiedAuthenticationService unifiedAuthenticationService;

    @Override
    @Transactional
    public AuthorResponse createAuthor(CreateAuthorRequest request) {

        // Check for duplicate name
        EntityExceptionUtils.requireNoDuplicate(
                authorRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(request.getName()),
                "Author", "name", request.getName()
        );
        // Convert DTO to entity
        Author author = authorMapper.toEntity(request);
        // Save to database
        authorRepository.save(author);
        // Publish audit event for author creation
        auditService.publishCreateEvent("Author", author.getPublicId().toString(), author, unifiedAuthenticationService.getCurrentUserKeycloakId());
        // Convert to response DTO
        return authorMapper.toResponse(author);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponse getAuthorByPublicId(UUID publicId) {

        Author author = authorRepository.findByPublicIdAndDeletedAtIsNull(publicId)
                .orElseThrow(() -> EntityNotFoundException.forPublicId("Author", publicId));
        return authorMapper.toResponse(author);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedAuthorResponse getAllAuthors(AuthorSearchRequest request) {
        // Create pageable with sorting
        Pageable pageable = request.toPageable();
        Page<Author> authorPage;
        // If name filter is provided, search by name; otherwise get all authors
        if (StringUtils.hasText(request.getName())) {
            authorPage = authorRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(request.getName().trim(), pageable);
        } else {
            authorPage = authorRepository.findByDeletedAtIsNull(pageable);
        }
        return authorMapper.toPagedResponse(authorPage);
    }

    @Override
    @Transactional
    public AuthorResponse updateAuthor(UUID publicId, UpdateAuthorRequest request) {

        Author existingAuthor = authorRepository.findByPublicIdAndDeletedAtIsNull(publicId)
                .orElseThrow(() -> EntityNotFoundException.forPublicId("Author", publicId));
        // Store old values for audit
        Author oldAuthor = new Author();
        oldAuthor.setId(existingAuthor.getId());
        oldAuthor.setPublicId(existingAuthor.getPublicId());
        oldAuthor.setName(existingAuthor.getName());
        oldAuthor.setBiography(existingAuthor.getBiography());
        oldAuthor.setDeletedAt(existingAuthor.getDeletedAt());
        // Check for duplicate name (excluding current author)
        authorRepository.findByNameIgnoreCaseAndDeletedAtIsNull(request.getName())
                .ifPresent(author -> {
                    if (!author.getPublicId().equals(publicId)) {
                        throw EntityValidationException.duplicateValue("Author", "name", request.getName());
                    }
                });
        // Update entity with new data
        authorMapper.updateEntity(existingAuthor, request);
        // Save updated entity
        authorRepository.save(existingAuthor);
        // Publish audit event for author update
        auditService.publishUpdateEvent("Author", existingAuthor.getPublicId().toString(), oldAuthor, existingAuthor, unifiedAuthenticationService.getCurrentUserKeycloakId());
        return authorMapper.toResponse(existingAuthor);
    }

    @Override
    @Transactional
    public void deleteAuthor(UUID publicId) {

        // Check if author exists and is not already deleted
        Author existingAuthor = authorRepository.findByPublicIdAndDeletedAtIsNull(publicId)
                .orElseThrow(() -> EntityNotFoundException.forPublicId("Author", publicId));

        // Store old values for audit before deletion
        Author oldAuthor = new Author();
        oldAuthor.setId(existingAuthor.getId());
        oldAuthor.setPublicId(existingAuthor.getPublicId());
        oldAuthor.setName(existingAuthor.getName());
        oldAuthor.setBiography(existingAuthor.getBiography());
        oldAuthor.setDeletedAt(existingAuthor.getDeletedAt());

        // Perform timestamp-based soft deletion using repository method
        LocalDateTime now = LocalDateTime.now();
        authorRepository.softDeleteByPublicId(publicId, now, now, unifiedAuthenticationService.getCurrentUserKeycloakId());
        // Publish audit event for author deletion
        auditService.publishDeleteEvent("Author", existingAuthor.getPublicId().toString(), oldAuthor, unifiedAuthenticationService.getCurrentUserKeycloakId());
    }
}
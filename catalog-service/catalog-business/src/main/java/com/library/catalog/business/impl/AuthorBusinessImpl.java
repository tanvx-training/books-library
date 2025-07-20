package com.library.catalog.business.impl;

import com.library.catalog.business.aop.exception.EntityNotFoundException;
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

@Service
@Transactional
@RequiredArgsConstructor
public class AuthorBusinessImpl implements AuthorBusiness {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final AuditService auditService;

    @Override
    @Transactional
    public AuthorResponse createAuthor(CreateAuthorRequest request, String currentUser) {

        // Validate required fields
        EntityExceptionUtils.requireNonEmpty(request.getName(), "Author", "name");

        // Check for duplicate name
        EntityExceptionUtils.requireNoDuplicate(
                authorRepository.existsByNameIgnoreCaseAndDeleteFlagFalse(request.getName()),
                "Author", "name", request.getName()
        );

        // Convert DTO to entity
        Author author = authorMapper.toEntity(request);
        // Set audit fields
        author.setCreatedBy(currentUser);
        author.setUpdatedBy(currentUser);
        // Save to database
        Author savedAuthor = authorRepository.save(author);

        // Publish audit event for author creation
        auditService.publishCreateEvent("Author", savedAuthor.getId().toString(), savedAuthor, currentUser);

        // Convert to response DTO
        return authorMapper.toResponse(savedAuthor);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponse getAuthorById(Integer id) {

        EntityExceptionUtils.requireNonNull(id, "Author", "id");

        Author author = authorRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> EntityNotFoundException.forEntity("Author", id));

        return authorMapper.toResponse(author);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedAuthorResponse getAllAuthors(Pageable pageable) {

        // Retrieve authors from database
        Page<Author> authorPage = authorRepository.findByDeleteFlagFalse(pageable);
        // Convert to response DTO
        return authorMapper.toPagedResponse(authorPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedAuthorResponse searchAuthorsByName(String name, Pageable pageable) {

        String searchName = EntityExceptionUtils.requireNonEmpty(name, "Author", "search name");

        // Search authors by name
        Page<Author> authorPage = authorRepository.findByNameContainingIgnoreCaseAndDeleteFlagFalse(
                searchName, pageable);
        return authorMapper.toPagedResponse(authorPage);
    }

    @Override
    @Transactional
    public AuthorResponse updateAuthor(Integer id, UpdateAuthorRequest request, String currentUser) {

        EntityExceptionUtils.requireNonNull(id, "Author", "id");
        EntityExceptionUtils.requireNonEmpty(request.getName(), "Author", "name");

        Author existingAuthor = authorRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> EntityNotFoundException.forEntity("Author", id));

        // Store old values for audit
        Author oldAuthor = new Author();
        oldAuthor.setId(existingAuthor.getId());
        oldAuthor.setName(existingAuthor.getName());
        oldAuthor.setBiography(existingAuthor.getBiography());
        oldAuthor.setDeleteFlag(existingAuthor.getDeleteFlag());

        // Check for duplicate name (excluding current author)
        authorRepository.findByNameIgnoreCaseAndDeleteFlagFalse(request.getName())
                .ifPresent(author -> {
                    if (!author.getId().equals(id)) {
                        throw EntityValidationException.duplicateValue("Author", "name", request.getName());
                    }
                });

        // Update entity with new data
        authorMapper.updateEntity(existingAuthor, request);
        // Set audit fields
        existingAuthor.setUpdatedBy(currentUser);
        // Save updated entity
        authorRepository.save(existingAuthor);

        // Publish audit event for author update
        auditService.publishUpdateEvent("Author", existingAuthor.getId().toString(), oldAuthor, existingAuthor, currentUser);

        return authorMapper.toResponse(existingAuthor);
    }

    @Override
    @Transactional
    public void deleteAuthor(Integer id, String currentUser) {

        EntityExceptionUtils.requireNonNull(id, "Author", "id");

        Author existingAuthor = authorRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> EntityNotFoundException.forEntity("Author", id));

        // Store old values for audit before deletion
        Author oldAuthor = new Author();
        oldAuthor.setId(existingAuthor.getId());
        oldAuthor.setName(existingAuthor.getName());
        oldAuthor.setBiography(existingAuthor.getBiography());
        oldAuthor.setDeleteFlag(existingAuthor.getDeleteFlag());

        // Perform soft deletion
        existingAuthor.setDeleteFlag(true);
        existingAuthor.setUpdatedBy(currentUser);
        // Save updated entity
        authorRepository.save(existingAuthor);

        // Publish audit event for author deletion
        auditService.publishDeleteEvent("Author", existingAuthor.getId().toString(), oldAuthor, currentUser);
    }
}
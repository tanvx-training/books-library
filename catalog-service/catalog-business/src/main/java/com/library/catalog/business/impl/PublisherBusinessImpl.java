package com.library.catalog.business.impl;

import com.library.catalog.business.PublisherBusiness;
import com.library.catalog.business.aop.exception.EntityNotFoundException;
import com.library.catalog.business.dto.request.CreatePublisherRequest;
import com.library.catalog.business.dto.request.PublisherSearchRequest;
import com.library.catalog.business.dto.request.UpdatePublisherRequest;
import com.library.catalog.business.dto.response.PublisherResponse;
import com.library.catalog.business.dto.response.PagedPublisherResponse;
import com.library.catalog.business.kafka.publisher.AuditService;
import com.library.catalog.business.mapper.PublisherMapper;
import com.library.catalog.business.security.UnifiedAuthenticationService;
import com.library.catalog.business.util.EntityExceptionUtils;
import com.library.catalog.repository.PublisherRepository;
import com.library.catalog.repository.entity.Publisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublisherBusinessImpl implements PublisherBusiness {

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;
    private final AuditService auditService;
    private final UnifiedAuthenticationService unifiedAuthenticationService;

    @Override
    @Transactional
    public PublisherResponse createPublisher(CreatePublisherRequest request) {

        // Check for duplicate name
        EntityExceptionUtils.requireNoDuplicate(
                publisherRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(request.getName()),
                "Publisher", "name", request.getName()
        );
        // Convert DTO to entity
        Publisher publisher = publisherMapper.toEntity(request);
        // Save to database
        Publisher savedPublisher = publisherRepository.save(publisher);

        // Publish audit event for publisher creation
        auditService.publishCreateEvent("Publisher", savedPublisher.getPublicId().toString(), savedPublisher, unifiedAuthenticationService.getCurrentUserKeycloakId());

        // Convert to response DTO
        return publisherMapper.toResponse(savedPublisher);
    }

    @Override
    @Transactional(readOnly = true)
    public PublisherResponse getPublisherByPublicId(UUID publicId) {

        Publisher publisher = publisherRepository.findByPublicIdAndDeletedAtIsNull(publicId)
                .orElseThrow(() -> EntityNotFoundException.forEntity("Publisher", publicId));

        return publisherMapper.toResponse(publisher);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedPublisherResponse getAllPublishers(PublisherSearchRequest request) {

        // Create pageable object
        Pageable pageable = request.toPageable();
        Page<Publisher> publisherPage;
        // If name filter is provided, search by name; otherwise get all
        if (StringUtils.hasText(request.getName())) {
            publisherPage = publisherRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(request.getName().trim(), pageable);
        } else {
            publisherPage = publisherRepository.findByDeletedAtIsNull(pageable);
        }
        // Convert to response DTO
        return publisherMapper.toPagedResponse(publisherPage);
    }

    @Override
    @Transactional
    public PublisherResponse updatePublisher(UUID publicId, UpdatePublisherRequest request) {

        // Find existing publisher
        Publisher existingPublisher = publisherRepository.findByPublicIdAndDeletedAtIsNull(publicId)
                .orElseThrow(() -> EntityNotFoundException.forEntity("Publisher", publicId));

        // Store old values for audit event
        Publisher oldPublisher = getOldPublisher(existingPublisher);
        // Check for duplicate name (excluding current publisher)
        boolean isDuplicate = publisherRepository.findByNameIgnoreCaseAndDeletedAtIsNull(request.getName())
                .map(publisher -> !publisher.getPublicId().equals(publicId))
                .orElse(false);
        EntityExceptionUtils.requireNoDuplicate(isDuplicate, "Publisher", "name", request.getName());
        // Update entity with new values
        publisherMapper.updateEntity(existingPublisher, request);
        // Save updated publisher
        Publisher updatedPublisher = publisherRepository.save(existingPublisher);
        // Publish audit event with old and new values
        auditService.publishUpdateEvent("Publisher", updatedPublisher.getPublicId().toString(), 
                oldPublisher, updatedPublisher, unifiedAuthenticationService.getCurrentUserKeycloakId());
        // Convert to response DTO
        return publisherMapper.toResponse(updatedPublisher);
    }

    @Override
    @Transactional
    public void deletePublisher(UUID publicId) {

        Publisher existingPublisher = publisherRepository.findByPublicIdAndDeletedAtIsNull(publicId)
                .orElseThrow(() -> EntityNotFoundException.forEntity("Publisher", publicId));
        // Store old values for audit before deletion
        Publisher oldPublisher = getOldPublisher(existingPublisher);
        // Perform soft deletion using timestamp
        existingPublisher.markAsDeleted();
        existingPublisher.setUpdatedBy(unifiedAuthenticationService.getCurrentUserKeycloakId());
        // Save updated entity
        publisherRepository.save(existingPublisher);
        // Publish audit event for publisher deletion
        auditService.publishDeleteEvent("Publisher", existingPublisher.getPublicId().toString(), oldPublisher, unifiedAuthenticationService.getCurrentUserKeycloakId());
    }

    private static Publisher getOldPublisher(Publisher existingPublisher) {
        Publisher oldPublisher = new Publisher();
        oldPublisher.setId(existingPublisher.getId());
        oldPublisher.setPublicId(existingPublisher.getPublicId());
        oldPublisher.setName(existingPublisher.getName());
        oldPublisher.setAddress(existingPublisher.getAddress());
        oldPublisher.setDeletedAt(existingPublisher.getDeletedAt());
        oldPublisher.setCreatedAt(existingPublisher.getCreatedAt());
        oldPublisher.setUpdatedAt(existingPublisher.getUpdatedAt());
        oldPublisher.setCreatedBy(existingPublisher.getCreatedBy());
        oldPublisher.setUpdatedBy(existingPublisher.getUpdatedBy());
        return oldPublisher;
    }
}
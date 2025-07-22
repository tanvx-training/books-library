package com.library.catalog.business.impl;

import com.library.catalog.business.PublisherBusiness;
import com.library.catalog.business.aop.exception.EntityNotFoundException;
import com.library.catalog.business.dto.request.CreatePublisherRequest;
import com.library.catalog.business.dto.request.UpdatePublisherRequest;
import com.library.catalog.business.dto.response.PublisherResponse;
import com.library.catalog.business.dto.response.PagedPublisherResponse;
import com.library.catalog.business.kafka.publisher.AuditService;
import com.library.catalog.business.mapper.PublisherMapper;
import com.library.catalog.business.util.EntityExceptionUtils;
import com.library.catalog.repository.PublisherRepository;
import com.library.catalog.repository.entity.Publisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PublisherBusinessImpl implements PublisherBusiness {

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;
    private final AuditService auditService;

    @Override
    @Transactional
    public PublisherResponse createPublisher(CreatePublisherRequest request, String currentUser) {

        // Validate required fields
        EntityExceptionUtils.requireNonEmpty(request.getName(), "Publisher", "name");

        // Check for duplicate name
        EntityExceptionUtils.requireNoDuplicate(
                publisherRepository.existsByNameIgnoreCaseAndDeleteFlagFalse(request.getName()),
                "Publisher", "name", request.getName()
        );

        // Convert DTO to entity
        Publisher publisher = publisherMapper.toEntity(request);
        // Set audit fields
        publisher.setCreatedBy(currentUser);
        publisher.setUpdatedBy(currentUser);
        // Save to database
        Publisher savedPublisher = publisherRepository.save(publisher);

        // Publish audit event for publisher creation
        auditService.publishCreateEvent("Publisher", savedPublisher.getId().toString(), savedPublisher, currentUser);

        // Convert to response DTO
        return publisherMapper.toResponse(savedPublisher);
    }

    @Override
    @Transactional(readOnly = true)
    public PublisherResponse getPublisherById(Integer id) {

        EntityExceptionUtils.requireNonNull(id, "Publisher", "id");

        Publisher publisher = publisherRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> EntityNotFoundException.forEntity("Publisher", id));

        return publisherMapper.toResponse(publisher);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedPublisherResponse getAllPublishers(Pageable pageable) {

        // Retrieve publishers from database
        Page<Publisher> publisherPage = publisherRepository.findByDeleteFlagFalse(pageable);
        // Convert to response DTO
        return publisherMapper.toPagedResponse(publisherPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedPublisherResponse searchPublishersByName(String name, Pageable pageable) {

        String searchName = EntityExceptionUtils.requireNonEmpty(name, "Publisher", "search name");

        // Search publishers by name
        Page<Publisher> publisherPage = publisherRepository.findByNameContainingIgnoreCaseAndDeleteFlagFalse(
                searchName, pageable);
        return publisherMapper.toPagedResponse(publisherPage);
    }

    @Override
    @Transactional
    public PublisherResponse updatePublisher(Integer id, UpdatePublisherRequest request, String currentUser) {
        
        // Validate required parameters
        EntityExceptionUtils.requireNonNull(id, "Publisher", "id");
        EntityExceptionUtils.requireNonNull(request, "Publisher", "update request");
        EntityExceptionUtils.requireNonEmpty(request.getName(), "Publisher", "name");

        // Find existing publisher
        Publisher existingPublisher = publisherRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> EntityNotFoundException.forEntity("Publisher", id));

        // Store old values for audit event
        Publisher oldPublisher = new Publisher();
        oldPublisher.setId(existingPublisher.getId());
        oldPublisher.setName(existingPublisher.getName());
        oldPublisher.setAddress(existingPublisher.getAddress());
        oldPublisher.setCreatedAt(existingPublisher.getCreatedAt());
        oldPublisher.setUpdatedAt(existingPublisher.getUpdatedAt());
        oldPublisher.setCreatedBy(existingPublisher.getCreatedBy());
        oldPublisher.setUpdatedBy(existingPublisher.getUpdatedBy());

        // Check for duplicate name (excluding current publisher)
        EntityExceptionUtils.requireNoDuplicate(
                publisherRepository.existsByNameIgnoreCaseAndDeleteFlagFalseAndIdNot(request.getName(), id),
                "Publisher", "name", request.getName()
        );

        // Update entity with new values
        publisherMapper.updateEntity(existingPublisher, request);
        existingPublisher.setUpdatedBy(currentUser);

        // Save updated publisher
        Publisher updatedPublisher = publisherRepository.save(existingPublisher);

        // Publish audit event with old and new values
        auditService.publishUpdateEvent("Publisher", updatedPublisher.getId().toString(), 
                oldPublisher, updatedPublisher, currentUser);

        // Convert to response DTO
        return publisherMapper.toResponse(updatedPublisher);
    }

    @Override
    @Transactional
    public void deletePublisher(Integer id, String currentUser) {

        EntityExceptionUtils.requireNonNull(id, "Publisher", "id");

        Publisher existingPublisher = publisherRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> EntityNotFoundException.forEntity("Publisher", id));

        // Store old values for audit before deletion
        Publisher oldPublisher = getOldPublisher(existingPublisher);

        // Perform soft deletion
        existingPublisher.setDeleteFlag(true);
        existingPublisher.setUpdatedBy(currentUser);
        // Save updated entity
        publisherRepository.save(existingPublisher);

        // Publish audit event for publisher deletion
        auditService.publishDeleteEvent("Publisher", existingPublisher.getId().toString(), oldPublisher, currentUser);
    }

    private static Publisher getOldPublisher(Publisher existingPublisher) {
        Publisher oldPublisher = new Publisher();
        oldPublisher.setId(existingPublisher.getId());
        oldPublisher.setName(existingPublisher.getName());
        oldPublisher.setAddress(existingPublisher.getAddress());
        oldPublisher.setDeleteFlag(existingPublisher.getDeleteFlag());
        oldPublisher.setCreatedAt(existingPublisher.getCreatedAt());
        oldPublisher.setUpdatedAt(existingPublisher.getUpdatedAt());
        oldPublisher.setCreatedBy(existingPublisher.getCreatedBy());
        oldPublisher.setUpdatedBy(existingPublisher.getUpdatedBy());
        return oldPublisher;
    }
}
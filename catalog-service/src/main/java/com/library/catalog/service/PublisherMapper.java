package com.library.catalog.service;

import com.library.catalog.dto.request.CreatePublisherRequest;
import com.library.catalog.dto.request.UpdatePublisherRequest;
import com.library.catalog.dto.response.PagedPublisherResponse;
import com.library.catalog.dto.response.PublisherResponse;
import com.library.catalog.aop.InvalidUuidException;
import com.library.catalog.repository.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PublisherMapper {

    public UUID validateAndConvertUuid(String uuidString) {
        if (uuidString == null || uuidString.trim().isEmpty()) {
            throw new InvalidUuidException("UUID cannot be null or empty");
        }
        
        try {
            return UUID.fromString(uuidString.trim());
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("Invalid UUID format: " + uuidString);
        }
    }

    public String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }

    public boolean isValidForMapping(Publisher entity) {
        return entity != null && !entity.isDeleted();
    }

    public Publisher toEntity(CreatePublisherRequest request) {
        if (request == null) {
            return null;
        }

        Publisher publisher = new Publisher();
        publisher.setName(request.getName());
        publisher.setAddress(request.getAddress());
        return publisher;
    }

    public void updateEntity(Publisher entity, UpdatePublisherRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setName(request.getName());
        entity.setAddress(request.getAddress());
    }

    public PublisherResponse toResponse(Publisher entity) {
        if (entity == null) {
            return null;
        }

        // Skip mapping for soft-deleted entities (entities with deletedAt timestamp)
        if (entity.isDeleted()) {
            return null;
        }

        PublisherResponse response = new PublisherResponse();
        response.setPublicId(entity.getPublicId());
        response.setName(entity.getName());
        response.setAddress(entity.getAddress());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());

        return response;
    }

    public List<PublisherResponse> toResponseList(List<Publisher> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toResponse)
                .filter(Objects::nonNull) // Filter out null responses from soft-deleted entities
                .collect(Collectors.toList());
    }

    public PagedPublisherResponse toPagedResponse(Page<Publisher> page) {
        if (page == null) {
            return null;
        }

        PagedPublisherResponse response = new PagedPublisherResponse();
        
        // Convert content, filtering out null responses from soft-deleted entities
        List<PublisherResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        response.setContent(content);
        
        // Set pagination metadata - these values come from the database query
        // which already filters out soft-deleted records, so they remain accurate
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());

        return response;
    }
}
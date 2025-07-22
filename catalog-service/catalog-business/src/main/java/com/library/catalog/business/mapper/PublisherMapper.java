package com.library.catalog.business.mapper;

import com.library.catalog.repository.entity.Publisher;
import com.library.catalog.business.dto.request.CreatePublisherRequest;
import com.library.catalog.business.dto.request.UpdatePublisherRequest;
import com.library.catalog.business.dto.response.PublisherResponse;
import com.library.catalog.business.dto.response.PagedPublisherResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PublisherMapper {

    public Publisher toEntity(CreatePublisherRequest request) {
        if (request == null) {
            return null;
        }

        Publisher publisher = new Publisher();
        publisher.setName(request.getName());
        publisher.setAddress(request.getAddress());
        // deleteFlag is set to false by default in entity constructor
        // audit fields (createdAt, updatedAt, createdBy, updatedBy) are handled by JPA/service layer
        
        return publisher;
    }

    public void updateEntity(Publisher entity, UpdatePublisherRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setName(request.getName());
        entity.setAddress(request.getAddress());
        // audit fields (updatedAt, updatedBy) are handled by JPA/service layer
    }

    public PublisherResponse toResponse(Publisher entity) {
        if (entity == null) {
            return null;
        }

        PublisherResponse response = new PublisherResponse();
        response.setId(entity.getId());
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
                .collect(Collectors.toList());
    }

    public PagedPublisherResponse toPagedResponse(Page<Publisher> page) {
        if (page == null) {
            return null;
        }

        PagedPublisherResponse response = new PagedPublisherResponse();
        
        // Convert content
        List<PublisherResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        response.setContent(content);
        
        // Set pagination metadata
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());

        return response;
    }
}
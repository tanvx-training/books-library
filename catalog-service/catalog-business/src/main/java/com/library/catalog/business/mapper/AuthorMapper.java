package com.library.catalog.business.mapper;

import com.library.catalog.repository.entity.Author;
import com.library.catalog.business.dto.request.CreateAuthorRequest;
import com.library.catalog.business.dto.request.UpdateAuthorRequest;
import com.library.catalog.business.dto.response.AuthorResponse;
import com.library.catalog.business.dto.response.PagedAuthorResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorMapper {

    public Author toEntity(CreateAuthorRequest request) {
        if (request == null) {
            return null;
        }

        Author author = new Author();
        author.setName(request.getName());
        author.setBiography(request.getBiography());
        // deleteFlag is set to false by default in entity constructor
        // audit fields (createdAt, updatedAt, createdBy, updatedBy) are handled by JPA/service layer
        
        return author;
    }

    public void updateEntity(Author entity, UpdateAuthorRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setName(request.getName());
        entity.setBiography(request.getBiography());
        // audit fields (updatedAt, updatedBy) are handled by JPA/service layer
    }

    public AuthorResponse toResponse(Author entity) {
        if (entity == null) {
            return null;
        }

        AuthorResponse response = new AuthorResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setBiography(entity.getBiography());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());

        return response;
    }

    public List<AuthorResponse> toResponseList(List<Author> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PagedAuthorResponse toPagedResponse(Page<Author> page) {
        if (page == null) {
            return null;
        }

        PagedAuthorResponse response = new PagedAuthorResponse();
        
        // Convert content
        List<AuthorResponse> content = page.getContent().stream()
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
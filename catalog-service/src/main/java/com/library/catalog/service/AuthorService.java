package com.library.catalog.service;

import com.library.catalog.dto.request.AuthorSearchRequest;
import com.library.catalog.dto.request.CreateAuthorRequest;
import com.library.catalog.dto.request.UpdateAuthorRequest;
import com.library.catalog.dto.response.AuthorResponse;
import com.library.catalog.dto.response.PagedAuthorResponse;

import java.util.UUID;

public interface AuthorService {

    AuthorResponse createAuthor(CreateAuthorRequest request);

    AuthorResponse getAuthorByPublicId(UUID publicId);

    PagedAuthorResponse getAllAuthors(AuthorSearchRequest request);

    AuthorResponse updateAuthor(UUID publicId, UpdateAuthorRequest request);

    void deleteAuthor(UUID publicId);
}
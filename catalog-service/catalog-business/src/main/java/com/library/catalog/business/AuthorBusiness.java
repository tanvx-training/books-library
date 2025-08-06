package com.library.catalog.business;

import com.library.catalog.business.dto.request.AuthorSearchRequest;
import com.library.catalog.business.dto.request.CreateAuthorRequest;
import com.library.catalog.business.dto.request.UpdateAuthorRequest;
import com.library.catalog.business.dto.response.AuthorResponse;
import com.library.catalog.business.dto.response.PagedAuthorResponse;

import java.util.UUID;

public interface AuthorBusiness {

    AuthorResponse createAuthor(CreateAuthorRequest request);

    AuthorResponse getAuthorByPublicId(UUID publicId);

    PagedAuthorResponse getAllAuthors(AuthorSearchRequest request);

    AuthorResponse updateAuthor(UUID publicId, UpdateAuthorRequest request);

    void deleteAuthor(UUID publicId);
}
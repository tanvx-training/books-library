package com.library.catalog.business;

import com.library.catalog.business.dto.request.CreateAuthorRequest;
import com.library.catalog.business.dto.request.UpdateAuthorRequest;
import com.library.catalog.business.dto.response.AuthorResponse;
import com.library.catalog.business.dto.response.PagedAuthorResponse;
import org.springframework.data.domain.Pageable;

public interface AuthorBusiness {

    AuthorResponse createAuthor(CreateAuthorRequest request, String currentUser);

    AuthorResponse getAuthorById(Integer id);

    PagedAuthorResponse getAllAuthors(Pageable pageable);

    PagedAuthorResponse searchAuthorsByName(String name, Pageable pageable);

    AuthorResponse updateAuthor(Integer id, UpdateAuthorRequest request, String currentUser);

    void deleteAuthor(Integer id, String currentUser);
}
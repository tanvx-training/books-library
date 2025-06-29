package com.library.book.service;

import com.library.book.dto.request.AuthorCreateDTO;
import com.library.book.dto.response.AuthorResponseDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;

import java.util.List;

public interface AuthorService {

    PaginatedResponse<AuthorResponseDTO> getAllAuthors(PaginatedRequest paginatedRequest);

    AuthorResponseDTO createAuthor(AuthorCreateDTO authorCreateDTO);

    List<BookResponseDTO> getBooksByAuthor(Long authorId);
}

package com.library.book.domain.service;

import com.library.book.presentation.dto.request.AuthorCreateDTO;
import com.library.book.presentation.dto.response.AuthorResponseDTO;
import com.library.book.presentation.dto.response.BookResponseDTO;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;

import java.util.List;

public interface AuthorService {

    PageResponseDTO<AuthorResponseDTO> getAllAuthors(PageRequestDTO pageRequestDTO);

    AuthorResponseDTO createAuthor(AuthorCreateDTO authorCreateDTO);

    List<BookResponseDTO> getBooksByAuthor(Long authorId);
}

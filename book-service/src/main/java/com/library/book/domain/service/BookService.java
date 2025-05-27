package com.library.book.domain.service;

import com.library.book.presentation.dto.request.BookCreateDTO;
import com.library.book.presentation.dto.response.BookResponseDTO;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import jakarta.validation.Valid;

public interface BookService {

    PageResponseDTO<BookResponseDTO> getAllBooks(PageRequestDTO pageRequestDTO);

    BookResponseDTO createBook(BookCreateDTO bookCreateDTO);

    BookResponseDTO getBookById(Long bookId);

    PageResponseDTO<BookResponseDTO> searchBooks(String keyword, PageRequestDTO pageRequestDTO);
}

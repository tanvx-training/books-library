package com.library.book.service;

import com.library.book.dto.request.BookCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;

public interface BookService {

    PageResponseDTO<BookResponseDTO> getAllBooks(PageRequestDTO pageRequestDTO);

    BookResponseDTO createBook(BookCreateDTO bookCreateDTO);

    BookResponseDTO getBookById(Long bookId);

    PageResponseDTO<BookResponseDTO> searchBooks(String keyword, PageRequestDTO pageRequestDTO);
}

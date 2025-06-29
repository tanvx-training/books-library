package com.library.book.service;

import com.library.book.dto.request.BookCreateDTO;
import com.library.book.dto.response.BookResponseDTO;
import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;

public interface BookService {

    PaginatedResponse<BookResponseDTO> getAllBooks(PaginatedRequest paginatedRequest);

    BookResponseDTO createBook(BookCreateDTO bookCreateDTO);

    BookResponseDTO getBookById(Long bookId);

    PaginatedResponse<BookResponseDTO> searchBooks(String keyword, PaginatedRequest paginatedRequest);
}

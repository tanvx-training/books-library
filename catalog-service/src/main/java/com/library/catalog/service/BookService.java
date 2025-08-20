package com.library.catalog.service;

import com.library.catalog.dto.request.BookSearchRequest;
import com.library.catalog.dto.request.UpdateBookWithCopiesRequest;
import com.library.catalog.dto.response.BookResponse;
import com.library.catalog.dto.response.BookDetailResponse;
import com.library.catalog.dto.request.CreateBookRequest;
import com.library.catalog.dto.request.CreateBookWithCopiesRequest;
import com.library.catalog.dto.response.PagedBookResponse;

import java.util.UUID;

public interface BookService {

    BookResponse createBook(CreateBookRequest request);

    BookDetailResponse createBookWithCopies(CreateBookWithCopiesRequest request);

    BookDetailResponse getBookDetail(UUID publicId);

    PagedBookResponse searchBooks(BookSearchRequest request);

    BookDetailResponse updateBookWithCopies(UUID publicId, UpdateBookWithCopiesRequest request);

    void deleteBook(UUID publicId);
}
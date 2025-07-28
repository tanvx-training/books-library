package com.library.catalog.business;

import com.library.catalog.business.dto.request.BookSearchRequest;
import com.library.catalog.business.dto.request.CreateBookRequest;
import com.library.catalog.business.dto.request.UpdateBookRequest;
import com.library.catalog.business.dto.response.BookDetailResponse;
import com.library.catalog.business.dto.response.BookResponse;
import com.library.catalog.business.dto.response.PagedBookResponse;

import java.util.UUID;

public interface BookBusiness {

    BookResponse createBook(CreateBookRequest request, String currentUser);

    BookDetailResponse getBookDetail(UUID publicId);

    PagedBookResponse searchBooks(BookSearchRequest request);

    BookResponse updateBook(UUID publicId, UpdateBookRequest request, String currentUser);

    void deleteBook(UUID publicId, String currentUser);
}
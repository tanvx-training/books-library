package com.library.catalog.business;

import com.library.catalog.business.dto.request.BookSearchRequest;
import com.library.catalog.business.dto.request.CreateBookRequest;
import com.library.catalog.business.dto.request.CreateBookWithCopiesRequest;
import com.library.catalog.business.dto.request.UpdateBookWithCopiesRequest;
import com.library.catalog.business.dto.response.BookDetailResponse;
import com.library.catalog.business.dto.response.BookResponse;
import com.library.catalog.business.dto.response.PagedBookResponse;

import java.util.UUID;

public interface BookBusiness {

    BookResponse createBook(CreateBookRequest request);

    BookDetailResponse createBookWithCopies(CreateBookWithCopiesRequest request);

    BookDetailResponse getBookDetail(UUID publicId);

    PagedBookResponse searchBooks(BookSearchRequest request);

    BookDetailResponse updateBookWithCopies(UUID publicId, UpdateBookWithCopiesRequest request);

    void deleteBook(UUID publicId);
}
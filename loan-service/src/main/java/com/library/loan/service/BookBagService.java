package com.library.loan.service;

import com.library.loan.dto.request.AddBookToBagRequest;
import com.library.loan.dto.request.UpdateBookQuantityRequest;
import com.library.loan.dto.response.BookBagResponse;

public interface BookBagService {

    BookBagResponse getUserBookBag(String userPublicId);

    BookBagResponse addBookToBag(String userPublicId, AddBookToBagRequest request);

    BookBagResponse updateBookQuantity(String userPublicId, String bookPublicId, UpdateBookQuantityRequest request);

    void removeBookFromBag(String userPublicId, String bookPublicId);

    void clearBookBag(String userPublicId);

    long getBookBagItemCount(String userPublicId);
}
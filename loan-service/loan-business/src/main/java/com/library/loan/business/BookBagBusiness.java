package com.library.loan.business;

import com.library.loan.business.dto.request.AddBookToBagRequest;
import com.library.loan.business.dto.request.UpdateBookQuantityRequest;
import com.library.loan.business.dto.response.BookBagResponse;

public interface BookBagBusiness {

    BookBagResponse getUserBookBag(String userPublicId);

    BookBagResponse addBookToBag(String userPublicId, AddBookToBagRequest request);

    BookBagResponse updateBookQuantity(String userPublicId, String bookPublicId, UpdateBookQuantityRequest request);

    void removeBookFromBag(String userPublicId, String bookPublicId);

    void clearBookBag(String userPublicId);

    long getBookBagItemCount(String userPublicId);
}
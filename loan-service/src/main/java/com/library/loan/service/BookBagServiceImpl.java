package com.library.loan.service;

import com.library.loan.dto.request.AddBookToBagRequest;
import com.library.loan.dto.request.UpdateBookQuantityRequest;
import com.library.loan.dto.response.BookBagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookBagServiceImpl implements BookBagService {
    @Override
    public BookBagResponse getUserBookBag(String userPublicId) {
        return null;
    }

    @Override
    public BookBagResponse addBookToBag(String userPublicId, AddBookToBagRequest request) {
        return null;
    }

    @Override
    public BookBagResponse updateBookQuantity(String userPublicId, String bookPublicId, UpdateBookQuantityRequest request) {
        return null;
    }

    @Override
    public void removeBookFromBag(String userPublicId, String bookPublicId) {

    }

    @Override
    public void clearBookBag(String userPublicId) {

    }

    @Override
    public long getBookBagItemCount(String userPublicId) {
        return 0;
    }
}
package com.library.loan.service;

import com.library.loan.dto.request.BorrowingSearchRequest;
import com.library.loan.dto.request.CreateBorrowingRequest;
import com.library.loan.dto.request.RenewBorrowingRequest;
import com.library.loan.dto.request.ReturnBookRequest;
import com.library.loan.dto.response.BorrowingResponse;
import com.library.loan.dto.response.PagedBorrowingResponse;

import java.util.UUID;

public interface BorrowingService {

    PagedBorrowingResponse getAllBorrowings(BorrowingSearchRequest request);

    BorrowingResponse getBorrowingByPublicId(UUID publicId);

    BorrowingResponse createBorrowing(CreateBorrowingRequest request);

    BorrowingResponse returnBook(UUID publicId, ReturnBookRequest request);

    BorrowingResponse renewBorrowing(UUID publicId, RenewBorrowingRequest request);

    void deleteBorrowing(UUID publicId);
}
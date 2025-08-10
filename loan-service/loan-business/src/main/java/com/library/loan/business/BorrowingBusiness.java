package com.library.loan.business;

import com.library.loan.business.dto.request.BorrowingSearchRequest;
import com.library.loan.business.dto.request.CreateBorrowingRequest;
import com.library.loan.business.dto.request.RenewBorrowingRequest;
import com.library.loan.business.dto.request.ReturnBookRequest;
import com.library.loan.business.dto.response.BorrowingResponse;
import com.library.loan.business.dto.response.PagedBorrowingResponse;

import java.util.UUID;

public interface BorrowingBusiness {

    PagedBorrowingResponse getAllBorrowings(BorrowingSearchRequest request);

    BorrowingResponse getBorrowingByPublicId(UUID publicId);

    BorrowingResponse createBorrowing(CreateBorrowingRequest request);

    BorrowingResponse returnBook(UUID publicId, ReturnBookRequest request);

    BorrowingResponse renewBorrowing(UUID publicId, RenewBorrowingRequest request);

    void deleteBorrowing(UUID publicId);
}
package com.library.loan.business.impl;

import com.library.loan.business.BorrowingBusiness;
import com.library.loan.business.dto.request.BorrowingSearchRequest;
import com.library.loan.business.dto.request.CreateBorrowingRequest;
import com.library.loan.business.dto.request.RenewBorrowingRequest;
import com.library.loan.business.dto.request.ReturnBookRequest;
import com.library.loan.business.dto.response.BorrowingResponse;
import com.library.loan.business.dto.response.PagedBorrowingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowingBusinessImpl implements BorrowingBusiness {
    @Override
    public PagedBorrowingResponse getAllBorrowings(BorrowingSearchRequest request) {
        return null;
    }

    @Override
    public BorrowingResponse getBorrowingByPublicId(UUID publicId) {
        return null;
    }

    @Override
    public BorrowingResponse createBorrowing(CreateBorrowingRequest request) {
        return null;
    }

    @Override
    public BorrowingResponse returnBook(UUID publicId, ReturnBookRequest request) {
        return null;
    }

    @Override
    public BorrowingResponse renewBorrowing(UUID publicId, RenewBorrowingRequest request) {
        return null;
    }

    @Override
    public void deleteBorrowing(UUID publicId) {

    }
}
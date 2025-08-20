package com.library.loan.service;

import com.library.loan.dto.request.BorrowingSearchRequest;
import com.library.loan.dto.request.CreateBorrowingRequest;
import com.library.loan.dto.request.RenewBorrowingRequest;
import com.library.loan.dto.request.ReturnBookRequest;
import com.library.loan.dto.response.BorrowingResponse;
import com.library.loan.dto.response.PagedBorrowingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowingServiceImpl implements BorrowingService {
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
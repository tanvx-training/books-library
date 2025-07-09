package com.library.book.domain.repository;

import com.library.book.domain.model.bookcopy.BookCopy;
import com.library.book.domain.model.bookcopy.BookCopyId;
import com.library.book.domain.model.bookcopy.CopyNumber;

import java.util.List;
import java.util.Optional;

public interface BookCopyRepository {
    BookCopy save(BookCopy bookCopy);
    Optional<BookCopy> findById(BookCopyId id);
    List<BookCopy> findByBookId(Long bookId);
    boolean existsByBookIdAndCopyNumber(Long bookId, CopyNumber copyNumber);
    void delete(BookCopy bookCopy);
    long countActiveBookCopyBorrowings(BookCopyId bookCopyId);
} 
package com.library.book.infrastructure.persistence.impl;

import com.library.book.domain.model.bookcopy.BookCopy;
import com.library.book.domain.model.bookcopy.BookCopyId;
import com.library.book.domain.model.bookcopy.CopyNumber;
import com.library.book.domain.repository.BookCopyRepository;
import com.library.book.infrastructure.persistence.entity.BookCopyJpaEntity;
import com.library.book.infrastructure.persistence.mapper.BookCopyEntityMapper;
import com.library.book.infrastructure.persistence.repository.BookCopyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BookCopyRepositoryImpl implements BookCopyRepository {

    private final BookCopyJpaRepository bookCopyJpaRepository;
    private final BookCopyEntityMapper bookCopyEntityMapper;
    
    @Override
    public BookCopy save(BookCopy bookCopy) {
        BookCopyJpaEntity jpaEntity = bookCopyEntityMapper.toJpaEntity(bookCopy);
        BookCopyJpaEntity savedEntity = bookCopyJpaRepository.save(jpaEntity);
        return bookCopyEntityMapper.toDomainEntity(savedEntity);
    }
    
    @Override
    public Optional<BookCopy> findById(BookCopyId id) {
        return bookCopyJpaRepository.findById(id.getValue())
                .map(bookCopyEntityMapper::toDomainEntity);
    }
    
    @Override
    public List<BookCopy> findByBookId(Long bookId) {
        return bookCopyJpaRepository.findByBookId(bookId).stream()
                .map(bookCopyEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByBookIdAndCopyNumber(Long bookId, CopyNumber copyNumber) {
        return bookCopyJpaRepository.existsByBookIdAndCopyNumber(bookId, copyNumber.getValue());
    }
    
    @Override
    public void delete(BookCopy bookCopy) {
        bookCopyJpaRepository.deleteById(bookCopy.getId().getValue());
    }
    
    @Override
    public long countActiveBookCopyBorrowings(BookCopyId bookCopyId) {
        return bookCopyJpaRepository.countActiveBookCopyBorrowings(bookCopyId.getValue());
    }
}
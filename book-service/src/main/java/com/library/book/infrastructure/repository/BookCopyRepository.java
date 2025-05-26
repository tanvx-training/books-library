package com.library.book.infrastructure.repository;

import com.library.book.domain.model.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
}

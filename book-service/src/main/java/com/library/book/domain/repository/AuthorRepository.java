package com.library.book.domain.repository;

import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AuthorRepository {
    Author save(Author author);
    Optional<Author> findById(AuthorId id);
    Page<Author> findAll(Pageable pageable);
    long count();
    void delete(Author author);
}

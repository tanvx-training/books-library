package com.library.book.domain.repository;

import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.model.author.AuthorName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository {
    Author save(Author author);
    Optional<Author> findById(AuthorId id);
    Page<Author> findAll(Pageable pageable);
    List<Author> findAll();
    boolean existsByName(AuthorName name);
    long count();
    void delete(Author author);
}

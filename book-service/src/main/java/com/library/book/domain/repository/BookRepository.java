package com.library.book.domain.repository;

import com.library.book.domain.model.book.Book;
import com.library.book.domain.model.book.BookId;
import com.library.book.domain.model.category.CategoryId;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save(Book book);
    Optional<Book> findById(BookId id);
    Page<Book> findAll(int page, int size);
    Page<Book> findAllByTitle(String title, int page, int size);
    Page<Book> findAllByCategories(List<CategoryId> categoryIds, int page, int size);
    boolean existsByIsbn(String isbn);
    long count();
    void delete(Book book);
} 
package com.library.book.infrastructure.repository;

import com.library.book.domain.model.Category;
import com.library.book.domain.model.Book;
import com.library.book.domain.model.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findAllByCategories(List<Category> categories, Pageable pageable);

    Page<Book> findAllByPublisher(Publisher publisher, Pageable pageable);
}

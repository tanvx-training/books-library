package com.library.book.repository;

import com.library.book.model.Category;
import com.library.book.model.Book;
import com.library.book.model.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book>, com.library.book.repository.custom.BookRepositoryCustom {

    Page<Book> findAllByDeleteFlg(boolean deleteFlg, Pageable pageable);

    Page<Book> findAllByCategoriesAndDeleteFlg(List<Category> categories, boolean deleteFlg, Pageable pageable);

    Page<Book> findAllByPublisherAndDeleteFlg(Publisher publisher, boolean deleteFlg, Pageable pageable);

    boolean existsByIsbn(String isbn);
}

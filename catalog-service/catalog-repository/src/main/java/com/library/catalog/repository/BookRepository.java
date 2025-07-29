package com.library.catalog.repository;

import com.library.catalog.repository.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByPublicIdAndDeletedAtIsNull(UUID publicId);

    Optional<Book> findByIsbnAndDeletedAtIsNull(String isbn);

    boolean existsByPublicIdAndDeletedAtIsNull(UUID publicId);

    boolean existsByIsbnAndDeletedAtIsNull(String isbn);

    boolean existsByIsbnAndDeletedAtIsNullAndPublicIdNot(String isbn, UUID publicId);

    Page<Book> findByDeletedAtIsNull(Pageable pageable);

    List<Book> findByPublisherIdAndDeletedAtIsNull(Long publisherId);

    @Query("SELECT b FROM Book b " +
           "JOIN BookAuthor ba ON b.id = ba.bookId " +
           "JOIN Author a ON ba.authorId = a.id " +
           "WHERE a.publicId = :authorPublicId " +
           "AND b.deletedAt IS NULL " +
           "AND a.deletedAt IS NULL")
    List<Book> findByAuthorPublicId(@Param("authorPublicId") UUID authorPublicId);

    @Query("SELECT b FROM Book b " +
           "JOIN BookCategory bc ON b.id = bc.bookId " +
           "JOIN Category c ON bc.categoryId = c.id " +
           "WHERE c.publicId = :categoryPublicId " +
           "AND b.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL")
    List<Book> findByCategoryPublicId(@Param("categoryPublicId") UUID categoryPublicId);

    @Query("""
        SELECT DISTINCT b
        FROM Book b
          LEFT JOIN BookAuthor ba   ON b.id = ba.bookId
          LEFT JOIN Author a        ON ba.authorId = a.id
          LEFT JOIN BookCategory bc ON b.id = bc.bookId
          LEFT JOIN Category c      ON bc.categoryId = c.id
          LEFT JOIN Publisher p     ON b.publisherId = p.id
        WHERE b.deletedAt IS NULL
          AND (:title            IS NULL OR LOWER(CAST(b.title AS string))           LIKE LOWER(CONCAT('%', CAST(:title AS string), '%')))
          AND (:isbn             IS NULL OR b.isbn                 = :isbn)
          AND (:publisherName    IS NULL OR LOWER(CAST(p.name AS string))           LIKE LOWER(CONCAT('%', CAST(:publisherName AS string), '%')))
          AND (:authorName       IS NULL OR LOWER(CAST(a.name AS string))           LIKE LOWER(CONCAT('%', CAST(:authorName AS string), '%')))
          AND (:categoryName     IS NULL OR LOWER(CAST(c.name AS string))           LIKE LOWER(CONCAT('%', CAST(:categoryName AS string), '%')))
          AND (:publicationYear  IS NULL OR b.publicationYear       = :publicationYear)
          AND (:language         IS NULL OR LOWER(CAST(b.language AS string))        = LOWER(CAST(:language AS string)))
          AND p.deletedAt IS NULL
          AND (:authorName   IS NULL OR a.deletedAt IS NULL)
          AND (:categoryName IS NULL OR c.deletedAt IS NULL)
        """)
    Page<Book> searchBooks(@Param("title") String title,
                          @Param("isbn") String isbn,
                          @Param("publisherName") String publisherName,
                          @Param("authorName") String authorName,
                          @Param("categoryName") String categoryName,
                          @Param("publicationYear") Short publicationYear,
                          @Param("language") String language,
                          Pageable pageable);
}
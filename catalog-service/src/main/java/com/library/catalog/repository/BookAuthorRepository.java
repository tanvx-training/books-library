package com.library.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthor, BookAuthor.BookAuthorId> {

    // Find book-author associations by book ID
    List<BookAuthor> findByBookId(Long bookId);

    // Find book-author associations by author ID
    List<BookAuthor> findByAuthorId(Long authorId);

    // Find book-author associations by book ID with pagination
    Page<BookAuthor> findByBookId(Long bookId, Pageable pageable);

    // Find book-author associations by author ID with pagination
    Page<BookAuthor> findByAuthorId(Long authorId, Pageable pageable);

    // Find specific book-author association
    Optional<BookAuthor> findByBookIdAndAuthorId(Long bookId, Long authorId);

    // Check if book-author association exists
    boolean existsByBookIdAndAuthorId(Long bookId, Long authorId);

    // Check if book has any authors
    boolean existsByBookId(Long bookId);

    // Check if author has any books
    boolean existsByAuthorId(Long authorId);

    // Find associations by multiple book IDs
    List<BookAuthor> findByBookIdIn(List<Long> bookIds);

    // Find associations by multiple author IDs
    List<BookAuthor> findByAuthorIdIn(List<Long> authorIds);

    // Count associations for a book
    long countByBookId(Long bookId);

    // Count associations for an author
    long countByAuthorId(Long authorId);

    // Find authors for books with detailed information (filtering by author.deleted_at)
    @Query("SELECT ba FROM BookAuthor ba " +
           "JOIN Book b ON ba.bookId = b.id " +
           "JOIN Author a ON ba.authorId = a.id " +
           "WHERE ba.bookId = :bookId " +
           "AND b.deletedAt IS NULL " +
           "AND a.deletedAt IS NULL")
    List<BookAuthor> findAssociationsForBookWithActiveEntities(@Param("bookId") Long bookId);

    // Find books for authors with detailed information (filtering by author.deleted_at)
    @Query("SELECT ba FROM BookAuthor ba " +
           "JOIN Book b ON ba.bookId = b.id " +
           "JOIN Author a ON ba.authorId = a.id " +
           "WHERE ba.authorId = :authorId " +
           "AND b.deletedAt IS NULL " +
           "AND a.deletedAt IS NULL")
    List<BookAuthor> findAssociationsForAuthorWithActiveEntities(@Param("authorId") Long authorId);

    // Find associations where both book and author are active
    @Query("SELECT ba FROM BookAuthor ba " +
           "JOIN Book b ON ba.bookId = b.id " +
           "JOIN Author a ON ba.authorId = a.id " +
           "WHERE b.deletedAt IS NULL " +
           "AND a.deletedAt IS NULL")
    Page<BookAuthor> findAllAssociationsWithActiveEntities(Pageable pageable);

    // Hard delete all associations for a book
    @Modifying
    @Query("DELETE FROM BookAuthor ba WHERE ba.bookId = :bookId")
    void deleteByBookId(@Param("bookId") Long bookId);

    // Hard delete all associations for an author
    @Modifying
    @Query("DELETE FROM BookAuthor ba WHERE ba.authorId = :authorId")
    void deleteByAuthorId(@Param("authorId") Long authorId);

    // Hard delete specific book-author association
    @Modifying
    @Query("DELETE FROM BookAuthor ba WHERE ba.bookId = :bookId AND ba.authorId = :authorId")
    void deleteByBookIdAndAuthorId(@Param("bookId") Long bookId, @Param("authorId") Long authorId);

    // Validate multiple author IDs exist and are active (checking author.deleted_at)
    @Query("SELECT COUNT(a) FROM Author a WHERE a.id IN :authorIds AND a.deletedAt IS NULL")
    long countActiveAuthorsByIds(@Param("authorIds") List<Long> authorIds);

    // Get all active author IDs for validation (checking author.deleted_at)
    @Query("SELECT a.id FROM Author a WHERE a.id IN :authorIds AND a.deletedAt IS NULL")
    List<Long> findActiveAuthorIds(@Param("authorIds") List<Long> authorIds);

    // Find author internal IDs by public IDs (for public_id to internal ID resolution)
    @Query("SELECT a.id FROM Author a WHERE a.publicId IN :publicIds AND a.deletedAt IS NULL")
    List<Long> findAuthorIdsByPublicIds(@Param("publicIds") List<UUID> publicIds);

    // Count active authors by public IDs (for validation)
    @Query("SELECT COUNT(a) FROM Author a WHERE a.publicId IN :publicIds AND a.deletedAt IS NULL")
    long countActiveAuthorsByPublicIds(@Param("publicIds") List<UUID> publicIds);
}
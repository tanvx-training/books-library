package com.library.catalog.repository;

import com.library.catalog.repository.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Find active books with pagination
    Page<Book> findByDeletedAtIsNull(Pageable pageable);

    // Find active book by public_id
    Optional<Book> findByPublicIdAndDeletedAtIsNull(UUID publicId);

    // Find book by public_id including deleted ones (for internal use)
    Optional<Book> findByPublicId(UUID publicId);

    // Find active book by internal ID
    Optional<Book> findByIdAndDeletedAtIsNull(Long id);

    // Check if active book exists by public_id
    boolean existsByPublicIdAndDeletedAtIsNull(UUID publicId);

    // Check if active book exists by internal ID
    boolean existsByIdAndDeletedAtIsNull(Long id);

    // Check if active book exists by ISBN (case-insensitive)
    boolean existsByIsbnIgnoreCaseAndDeletedAtIsNull(String isbn);

    // Check if book with ISBN exists excluding specific public_id (for update validation)
    boolean existsByIsbnIgnoreCaseAndDeletedAtIsNullAndPublicIdNot(String isbn, UUID publicId);

    // Find active book by ISBN (case-insensitive)
    Optional<Book> findByIsbnIgnoreCaseAndDeletedAtIsNull(String isbn);

    // Search books by title (case-insensitive) excluding deleted ones
    Page<Book> findByTitleContainingIgnoreCaseAndDeletedAtIsNull(String title, Pageable pageable);

    // Search books by publication year excluding deleted ones
    Page<Book> findByPublicationYearAndDeletedAtIsNull(Short publicationYear, Pageable pageable);

    // Search books by publication year range excluding deleted ones
    Page<Book> findByPublicationYearBetweenAndDeletedAtIsNull(Short startYear, Short endYear, Pageable pageable);

    // Search books by language excluding deleted ones
    Page<Book> findByLanguageIgnoreCaseAndDeletedAtIsNull(String language, Pageable pageable);

    // Find books by publisher internal ID excluding deleted ones
    Page<Book> findByPublisherIdAndDeletedAtIsNull(Long publisherId, Pageable pageable);

    // Find books by publisher public_id through joins
    @Query("SELECT b FROM Book b " +
           "JOIN Publisher p ON b.publisherId = p.id " +
           "WHERE p.publicId = :publisherPublicId AND b.deletedAt IS NULL AND p.deletedAt IS NULL")
    Page<Book> findByPublisherPublicId(@Param("publisherPublicId") UUID publisherPublicId, Pageable pageable);

    // Find books by author public_id through junction table
    @Query("SELECT b FROM Book b " +
           "JOIN BookAuthor ba ON b.id = ba.bookId " +
           "JOIN Author a ON ba.authorId = a.id " +
           "WHERE a.publicId = :authorPublicId AND b.deletedAt IS NULL AND a.deletedAt IS NULL")
    Page<Book> findByAuthorPublicId(@Param("authorPublicId") UUID authorPublicId, Pageable pageable);

    // Find books by category public_id through junction table
    @Query("SELECT b FROM Book b " +
           "JOIN BookCategory bc ON b.id = bc.bookId " +
           "JOIN Category c ON bc.categoryId = c.id " +
           "WHERE c.publicId = :categoryPublicId AND b.deletedAt IS NULL AND c.deletedAt IS NULL")
    Page<Book> findByCategoryPublicId(@Param("categoryPublicId") UUID categoryPublicId, Pageable pageable);

    // Find books by author internal ID through junction table (for internal use)
    @Query("SELECT b FROM Book b " +
           "JOIN BookAuthor ba ON b.id = ba.bookId " +
           "WHERE ba.authorId = :authorId AND b.deletedAt IS NULL")
    Page<Book> findByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    // Find books by category internal ID through junction table (for internal use)
    @Query("SELECT b FROM Book b " +
           "JOIN BookCategory bc ON b.id = bc.bookId " +
           "WHERE bc.categoryId = :categoryId AND b.deletedAt IS NULL")
    Page<Book> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    // Find books by multiple criteria with custom query
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:isbn IS NULL OR LOWER(b.isbn) = LOWER(:isbn)) AND " +
           "(:publicationYear IS NULL OR b.publicationYear = :publicationYear) AND " +
           "(:language IS NULL OR LOWER(b.language) = LOWER(:language)) AND " +
           "(:publisherId IS NULL OR b.publisherId = :publisherId) AND " +
           "b.deletedAt IS NULL")
    Page<Book> findByCriteria(@Param("title") String title,
                              @Param("isbn") String isbn,
                              @Param("publicationYear") Integer publicationYear,
                              @Param("language") String language,
                              @Param("publisherId") Long publisherId,
                              Pageable pageable);

    // Complex search with multiple criteria including related entities using public_ids
    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN Publisher p ON b.publisherId = p.id AND p.deletedAt IS NULL " +
           "LEFT JOIN BookAuthor ba ON b.id = ba.bookId " +
           "LEFT JOIN Author a ON ba.authorId = a.id AND a.deletedAt IS NULL " +
           "LEFT JOIN BookCategory bc ON b.id = bc.bookId " +
           "LEFT JOIN Category c ON bc.categoryId = c.id AND c.deletedAt IS NULL " +
           "WHERE b.deletedAt IS NULL AND " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:isbn IS NULL OR LOWER(b.isbn) = LOWER(:isbn)) AND " +
           "(:publicationYear IS NULL OR b.publicationYear = :publicationYear) AND " +
           "(:language IS NULL OR LOWER(b.language) = LOWER(:language)) AND " +
           "(:publisherPublicId IS NULL OR p.publicId = :publisherPublicId) AND " +
           "(:authorPublicId IS NULL OR a.publicId = :authorPublicId) AND " +
           "(:categoryPublicId IS NULL OR c.publicId = :categoryPublicId)")
    Page<Book> findByComplexCriteria(@Param("title") String title,
                                     @Param("isbn") String isbn,
                                     @Param("publicationYear") Integer publicationYear,
                                     @Param("language") String language,
                                     @Param("publisherPublicId") UUID publisherPublicId,
                                     @Param("authorPublicId") UUID authorPublicId,
                                     @Param("categoryPublicId") UUID categoryPublicId,
                                     Pageable pageable);

    // Find books by author name through joins (for backward compatibility)
    @Query("SELECT b FROM Book b " +
           "JOIN BookAuthor ba ON b.id = ba.bookId " +
           "JOIN Author a ON ba.authorId = a.id " +
           "WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%')) " +
           "AND b.deletedAt IS NULL AND a.deletedAt IS NULL")
    Page<Book> findByAuthorNameContainingIgnoreCase(@Param("authorName") String authorName, Pageable pageable);

    // Find books by category name through joins (for backward compatibility)
    @Query("SELECT b FROM Book b " +
           "JOIN BookCategory bc ON b.id = bc.bookId " +
           "JOIN Category c ON bc.categoryId = c.id " +
           "WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%')) " +
           "AND b.deletedAt IS NULL AND c.deletedAt IS NULL")
    Page<Book> findByCategoryNameContainingIgnoreCase(@Param("categoryName") String categoryName, Pageable pageable);

    // Find books by publisher name through joins (for backward compatibility)
    @Query("SELECT b FROM Book b " +
           "JOIN Publisher p ON b.publisherId = p.id " +
           "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :publisherName, '%')) " +
           "AND b.deletedAt IS NULL AND p.deletedAt IS NULL")
    Page<Book> findByPublisherNameContainingIgnoreCase(@Param("publisherName") String publisherName, Pageable pageable);

    // Find all books by internal IDs (for bulk operations)
    List<Book> findByIdInAndDeletedAtIsNull(List<Long> ids);

    // Find all books by public IDs (for bulk operations)
    @Query("SELECT b FROM Book b WHERE b.publicId IN :publicIds AND b.deletedAt IS NULL")
    List<Book> findByPublicIdInAndDeletedAtIsNull(@Param("publicIds") List<UUID> publicIds);

    // Count active books
    long countByDeletedAtIsNull();

    // Count books by publisher internal ID
    long countByPublisherIdAndDeletedAtIsNull(Long publisherId);

    // Count books by publisher public_id
    @Query("SELECT COUNT(b) FROM Book b " +
           "JOIN Publisher p ON b.publisherId = p.id " +
           "WHERE p.publicId = :publisherPublicId AND b.deletedAt IS NULL AND p.deletedAt IS NULL")
    long countByPublisherPublicId(@Param("publisherPublicId") UUID publisherPublicId);

    // Count books by author public_id
    @Query("SELECT COUNT(b) FROM Book b " +
           "JOIN BookAuthor ba ON b.id = ba.bookId " +
           "JOIN Author a ON ba.authorId = a.id " +
           "WHERE a.publicId = :authorPublicId AND b.deletedAt IS NULL AND a.deletedAt IS NULL")
    long countByAuthorPublicId(@Param("authorPublicId") UUID authorPublicId);

    // Count books by category public_id
    @Query("SELECT COUNT(b) FROM Book b " +
           "JOIN BookCategory bc ON b.id = bc.bookId " +
           "JOIN Category c ON bc.categoryId = c.id " +
           "WHERE c.publicId = :categoryPublicId AND b.deletedAt IS NULL AND c.deletedAt IS NULL")
    long countByCategoryPublicId(@Param("categoryPublicId") UUID categoryPublicId);

    // Custom soft delete method using @Modifying query
    @Modifying
    @Query("UPDATE Book b SET b.deletedAt = :deletedAt, b.updatedAt = :updatedAt, b.updatedBy = :updatedBy WHERE b.publicId = :publicId AND b.deletedAt IS NULL")
    int softDeleteByPublicId(@Param("publicId") UUID publicId, 
                            @Param("deletedAt") LocalDateTime deletedAt,
                            @Param("updatedAt") LocalDateTime updatedAt, 
                            @Param("updatedBy") String updatedBy);

    // Validate multiple author IDs exist and are active (for relationship validation)
    @Query("SELECT COUNT(a) FROM Author a WHERE a.id IN :authorIds AND a.deletedAt IS NULL")
    long countActiveAuthorsByIds(@Param("authorIds") List<Long> authorIds);

    // Validate multiple category IDs exist and are active (for relationship validation)
    @Query("SELECT COUNT(c) FROM Category c WHERE c.id IN :categoryIds AND c.deletedAt IS NULL")
    long countActiveCategoriesByIds(@Param("categoryIds") List<Long> categoryIds);

    // Get active author IDs for validation
    @Query("SELECT a.id FROM Author a WHERE a.id IN :authorIds AND a.deletedAt IS NULL")
    List<Long> findActiveAuthorIds(@Param("authorIds") List<Long> authorIds);

    // Get active category IDs for validation
    @Query("SELECT c.id FROM Category c WHERE c.id IN :categoryIds AND c.deletedAt IS NULL")
    List<Long> findActiveCategoryIds(@Param("categoryIds") List<Long> categoryIds);
}
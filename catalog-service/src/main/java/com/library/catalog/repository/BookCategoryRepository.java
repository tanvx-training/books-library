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
public interface BookCategoryRepository extends JpaRepository<BookCategory, BookCategory.BookCategoryId> {

    // Find book-category associations by book ID
    List<BookCategory> findByBookId(Long bookId);

    // Find book-category associations by category ID
    List<BookCategory> findByCategoryId(Long categoryId);

    // Find book-category associations by book ID with pagination
    Page<BookCategory> findByBookId(Long bookId, Pageable pageable);

    // Find book-category associations by category ID with pagination
    Page<BookCategory> findByCategoryId(Long categoryId, Pageable pageable);

    // Find specific book-category association
    Optional<BookCategory> findByBookIdAndCategoryId(Long bookId, Long categoryId);

    // Check if book-category association exists
    boolean existsByBookIdAndCategoryId(Long bookId, Long categoryId);

    // Check if book has any categories
    boolean existsByBookId(Long bookId);

    // Check if category has any books
    boolean existsByCategoryId(Long categoryId);

    // Find associations by multiple book IDs
    List<BookCategory> findByBookIdIn(List<Long> bookIds);

    // Find associations by multiple category IDs
    List<BookCategory> findByCategoryIdIn(List<Long> categoryIds);

    // Count associations for a book
    long countByBookId(Long bookId);

    // Count associations for a category
    long countByCategoryId(Long categoryId);

    // Find categories for books with detailed information (filtering by category.deleted_at)
    @Query("SELECT bc FROM BookCategory bc " +
           "JOIN Book b ON bc.bookId = b.id " +
           "JOIN Category c ON bc.categoryId = c.id " +
           "WHERE bc.bookId = :bookId " +
           "AND b.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL")
    List<BookCategory> findAssociationsForBookWithActiveEntities(@Param("bookId") Long bookId);

    // Find books for categories with detailed information (filtering by category.deleted_at)
    @Query("SELECT bc FROM BookCategory bc " +
           "JOIN Book b ON bc.bookId = b.id " +
           "JOIN Category c ON bc.categoryId = c.id " +
           "WHERE bc.categoryId = :categoryId " +
           "AND b.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL")
    List<BookCategory> findAssociationsForCategoryWithActiveEntities(@Param("categoryId") Long categoryId);

    // Find associations where both book and category are active
    @Query("SELECT bc FROM BookCategory bc " +
           "JOIN Book b ON bc.bookId = b.id " +
           "JOIN Category c ON bc.categoryId = c.id " +
           "WHERE b.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL")
    Page<BookCategory> findAllAssociationsWithActiveEntities(Pageable pageable);

    // Hard delete all associations for a book
    @Modifying
    @Query("DELETE FROM BookCategory bc WHERE bc.bookId = :bookId")
    void deleteByBookId(@Param("bookId") Long bookId);

    // Hard delete all associations for a category
    @Modifying
    @Query("DELETE FROM BookCategory bc WHERE bc.categoryId = :categoryId")
    void deleteByCategoryId(@Param("categoryId") Long categoryId);

    // Hard delete specific book-category association
    @Modifying
    @Query("DELETE FROM BookCategory bc WHERE bc.bookId = :bookId AND bc.categoryId = :categoryId")
    void deleteByBookIdAndCategoryId(@Param("bookId") Long bookId, @Param("categoryId") Long categoryId);

    // Validate multiple category IDs exist and are active (checking category.deleted_at)
    @Query("SELECT COUNT(c) FROM Category c WHERE c.id IN :categoryIds AND c.deletedAt IS NULL")
    long countActiveCategoriesByIds(@Param("categoryIds") List<Long> categoryIds);

    // Get all active category IDs for validation (checking category.deleted_at)
    @Query("SELECT c.id FROM Category c WHERE c.id IN :categoryIds AND c.deletedAt IS NULL")
    List<Long> findActiveCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    // Find category internal IDs by public IDs (for public_id to internal ID resolution)
    @Query("SELECT c.id FROM Category c WHERE c.publicId IN :publicIds AND c.deletedAt IS NULL")
    List<Long> findCategoryIdsByPublicIds(@Param("publicIds") List<UUID> publicIds);

    // Count active categories by public IDs (for validation)
    @Query("SELECT COUNT(c) FROM Category c WHERE c.publicId IN :publicIds AND c.deletedAt IS NULL")
    long countActiveCategoriesByPublicIds(@Param("publicIds") List<UUID> publicIds);
}
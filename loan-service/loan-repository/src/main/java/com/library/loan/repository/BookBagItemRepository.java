package com.library.loan.repository;

import com.library.loan.repository.entity.BookBagItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookBagItemRepository extends JpaRepository<BookBagItem, Long> {

    List<BookBagItem> findByBookBagId(Long bookBagId);

    Optional<BookBagItem> findByBookBagIdAndBookId(Long bookBagId, Long bookId);

    @Query("SELECT bbi FROM BookBagItem bbi JOIN BookBag bb ON bbi.bookBagId = bb.id WHERE bb.userId = :userId")
    List<BookBagItem> findByUserId(@Param("userId") Long userId);

    @Query("SELECT bbi FROM BookBagItem bbi JOIN BookBag bb ON bbi.bookBagId = bb.id WHERE bb.userId = :userId AND bbi.bookId = :bookId")
    Optional<BookBagItem> findByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Query("SELECT COUNT(bbi) FROM BookBagItem bbi JOIN BookBag bb ON bbi.bookBagId = bb.id WHERE bb.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    void deleteByBookBagIdAndBookId(Long bookBagId, Long bookId);
}
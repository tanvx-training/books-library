package com.library.loan.repository;

import com.library.loan.repository.entity.BookBag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookBagRepository extends JpaRepository<BookBag, Long> {

    Optional<BookBag> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
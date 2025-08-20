package com.library.loan.repository;

import com.library.loan.repository.BookBag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookBagRepository extends JpaRepository<BookBag, Long> {

    Optional<BookBag> findByPublicId(UUID publicId);
}
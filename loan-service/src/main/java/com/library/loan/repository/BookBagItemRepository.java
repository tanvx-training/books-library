package com.library.loan.repository;

import com.library.loan.repository.BookBagItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookBagItemRepository extends JpaRepository<BookBagItem, Long> {
}
package com.library.book.repository;

import com.library.book.model.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    Page<Publisher> findAllByDeleteFlg(Boolean deleteFlg, Pageable pageable);

    boolean existsByName(String name);
}

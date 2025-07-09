package com.library.book.domain.repository;

import com.library.book.domain.model.publisher.Publisher;
import com.library.book.domain.model.publisher.PublisherId;
import com.library.book.domain.model.publisher.PublisherName;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface PublisherRepository {
    Publisher save(Publisher publisher);
    Optional<Publisher> findById(PublisherId id);
    Page<Publisher> findAll(int page, int size);
    long count();
    boolean existsByName(PublisherName name);
    void delete(Publisher publisher);
}
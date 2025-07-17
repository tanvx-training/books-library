package com.library.book.infrastructure.persistence.impl;

import com.library.book.domain.model.publisher.Publisher;
import com.library.book.domain.model.publisher.PublisherId;
import com.library.book.domain.model.publisher.PublisherName;
import com.library.book.domain.repository.PublisherRepository;
import com.library.book.infrastructure.exception.PublisherPersistenceException;
import com.library.book.infrastructure.persistence.entity.PublisherEntity;
import com.library.book.infrastructure.persistence.mapper.PublisherEntityMapper;
import com.library.book.infrastructure.persistence.repository.PublisherJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PublisherRepositoryImpl implements PublisherRepository {

    private final PublisherJpaRepository publisherJpaRepository;
    private final PublisherEntityMapper publisherEntityMapper;

    @Override
    public Publisher save(Publisher publisher) {
        try {
            PublisherEntity entity = publisherEntityMapper.toJpaEntity(publisher);
            PublisherEntity savedEntity = publisherJpaRepository.save(entity);
            return publisherEntityMapper.toDomainEntity(savedEntity);
        } catch (DataAccessException e) {
            log.error("Error saving publisher", e);
            throw new PublisherPersistenceException("Failed to save publisher", e);
        } catch (Exception e) {
            log.error("Unexpected error when saving publisher", e);
            throw new PublisherPersistenceException("Unexpected error when saving publisher", e);
        }
    }

    @Override
    public Optional<Publisher> findById(PublisherId id) {
        try {
            return publisherJpaRepository.findById(id.getValue())
                    .map(publisherEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding publisher by ID: {}", id.getValue(), e);
            throw new PublisherPersistenceException("Failed to find publisher by ID: " + id.getValue(), e);
        }
    }

    @Override
    public Page<Publisher> findAll(int page, int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name"));
            return publisherJpaRepository.findAllByDeleteFlg(false, pageRequest)
                    .map(publisherEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding all publishers", e);
            throw new PublisherPersistenceException("Failed to find all publishers", e);
        }
    }

    @Override
    public long count() {
        try {
            return publisherJpaRepository.count();
        } catch (DataAccessException e) {
            log.error("Error counting publishers", e);
            throw new PublisherPersistenceException("Failed to count publishers", e);
        }
    }

    @Override
    public boolean existsByName(PublisherName name) {
        try {
            return publisherJpaRepository.existsByName(name.getValue());
        } catch (DataAccessException e) {
            log.error("Error checking if publisher exists by name: {}", name.getValue(), e);
            throw new PublisherPersistenceException("Failed to check if publisher exists by name: " + name.getValue(), e);
        }
    }

    @Override
    public void delete(Publisher publisher) {
        try {
            // Soft delete
            PublisherEntity entity = publisherEntityMapper.toJpaEntity(publisher);
            entity.setDeleteFlg(true);
            publisherJpaRepository.save(entity);
        } catch (DataAccessException e) {
            log.error("Error deleting publisher", e);
            throw new PublisherPersistenceException("Failed to delete publisher", e);
        }
    }
}
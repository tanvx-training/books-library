package com.library.book.infrastructure.persistence.impl;

import com.library.book.domain.model.category.Category;
import com.library.book.domain.model.category.CategoryId;
import com.library.book.domain.model.category.CategoryName;
import com.library.book.domain.model.category.CategorySlug;
import com.library.book.domain.repository.CategoryRepository;
import com.library.book.infrastructure.exception.CategoryPersistenceException;
import com.library.book.infrastructure.persistence.entity.CategoryEntity;
import com.library.book.infrastructure.persistence.mapper.CategoryEntityMapper;
import com.library.book.infrastructure.persistence.repository.CategoryJpaRepository;
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
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryEntityMapper categoryEntityMapper;

    @Override
    public Category save(Category category) {
        try {
            CategoryEntity entity = categoryEntityMapper.toJpaEntity(category);
            CategoryEntity savedEntity = categoryJpaRepository.save(entity);
            return categoryEntityMapper.toDomainEntity(savedEntity);
        } catch (DataAccessException e) {
            log.error("Error saving category", e);
            throw new CategoryPersistenceException("Failed to save category", e);
        } catch (Exception e) {
            log.error("Unexpected error when saving category", e);
            throw new CategoryPersistenceException("Unexpected error when saving category", e);
        }
    }

    @Override
    public Optional<Category> findById(CategoryId id) {
        try {
            return categoryJpaRepository.findById(id.getValue())
                    .map(categoryEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding category by ID: {}", id.getValue(), e);
            throw new CategoryPersistenceException("Failed to find category by ID: " + id.getValue(), e);
        }
    }

    @Override
    public Page<Category> findAll(int page, int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name"));
            return categoryJpaRepository.findAllByDeleteFlg(false, pageRequest)
                    .map(categoryEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding all categories", e);
            throw new CategoryPersistenceException("Failed to find all categories", e);
        }
    }

    @Override
    public long count() {
        try {
            return categoryJpaRepository.count();
        } catch (DataAccessException e) {
            log.error("Error counting categories", e);
            throw new CategoryPersistenceException("Failed to count categories", e);
        }
    }

    @Override
    public boolean existsByNameOrSlug(CategoryName name, CategorySlug slug) {
        try {
            return categoryJpaRepository.existsByNameOrSlug(name.getValue(), slug.getValue());
        } catch (DataAccessException e) {
            log.error("Error checking if category exists by name/slug: {}/{}", name.getValue(), slug.getValue(), e);
            throw new CategoryPersistenceException("Failed to check if category exists by name/slug: " + name.getValue() + "/" + slug.getValue(), e);
        }
    }

    @Override
    public void delete(Category category) {
        try {
            // Soft delete
            CategoryEntity entity = categoryEntityMapper.toJpaEntity(category);
            entity.setDeleteFlg(true);
            categoryJpaRepository.save(entity);
        } catch (DataAccessException e) {
            log.error("Error deleting category", e);
            throw new CategoryPersistenceException("Failed to delete category", e);
        }
    }
}
package com.library.book.infrastructure.persistence.mapper;

import com.library.book.domain.model.category.*;
import com.library.book.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryEntityMapper {

    public CategoryEntity toJpaEntity(Category category) {
        CategoryEntity entity = new CategoryEntity();

        if (category.getId() != null && category.getId().getValue() != null) {
            entity.setId(category.getId().getValue());
        }

        entity.setName(category.getName().getValue());
        entity.setSlug(category.getSlug().getValue());
        entity.setDescription(category.getDescription().getValue());
        entity.setDeleteFlg(category.isDeleted());

        return entity;
    }

    public Category toDomainEntity(CategoryEntity jpaEntity) {
        // Sử dụng reflection hoặc constructor riêng để tạo Category từ JPA entity
        Category category = Category.create(
                CategoryName.of(jpaEntity.getName()),
                CategorySlug.of(jpaEntity.getSlug()),
                CategoryDescription.of(jpaEntity.getDescription()),
                "system" // Default user for reconstruction
        );

        // Reflection để set ID (trong thực tế nên có setter package-private)
        try {
            java.lang.reflect.Field idField = Category.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(category, new CategoryId(jpaEntity.getId()));

            java.lang.reflect.Field deletedField = Category.class.getDeclaredField("deleted");
            deletedField.setAccessible(true);
            deletedField.set(category, jpaEntity.isDeleteFlg());

            // Clear events since this is loading from DB
            category.clearEvents();

        } catch (Exception e) {
            throw new RuntimeException("Error mapping JPA entity to domain entity", e);
        }

        return category;
    }
}
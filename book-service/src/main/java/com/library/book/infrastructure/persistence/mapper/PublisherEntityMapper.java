package com.library.book.infrastructure.persistence.mapper;

import com.library.book.domain.model.publisher.Address;
import com.library.book.domain.model.publisher.Publisher;
import com.library.book.domain.model.publisher.PublisherId;
import com.library.book.domain.model.publisher.PublisherName;
import com.library.book.infrastructure.persistence.entity.PublisherJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PublisherEntityMapper {

    public PublisherJpaEntity toJpaEntity(Publisher publisher) {
        PublisherJpaEntity entity = new PublisherJpaEntity();

        if (publisher.getId() != null && publisher.getId().getValue() != null) {
            entity.setId(publisher.getId().getValue());
        }

        entity.setName(publisher.getName().getValue());
        entity.setAddress(publisher.getAddress().getValue());
        entity.setDeleteFlg(publisher.isDeleted());

        return entity;
    }

    public Publisher toDomainEntity(PublisherJpaEntity jpaEntity) {
        // Sử dụng reflection hoặc constructor riêng để tạo Publisher từ JPA entity
        Publisher publisher = Publisher.create(
                PublisherName.of(jpaEntity.getName()),
                Address.of(jpaEntity.getAddress())
        );

        // Reflection để set ID (trong thực tế nên có setter package-private)
        try {
            java.lang.reflect.Field idField = Publisher.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(publisher, new PublisherId(jpaEntity.getId()));

            java.lang.reflect.Field deletedField = Publisher.class.getDeclaredField("deleted");
            deletedField.setAccessible(true);
            deletedField.set(publisher, jpaEntity.isDeleteFlg());

            // Clear events since this is loading from DB
            publisher.clearEvents();

        } catch (Exception e) {
            throw new RuntimeException("Error mapping JPA entity to domain entity", e);
        }

        return publisher;
    }
}
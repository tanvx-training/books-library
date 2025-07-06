package com.library.book.infrastructure.persistence.mapper;

import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.model.author.AuthorName;
import com.library.book.domain.model.author.Biography;
import com.library.book.infrastructure.persistence.entity.AuthorJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthorEntityMapper {

    public AuthorJpaEntity toJpaEntity(Author author) {

        AuthorJpaEntity entity = new AuthorJpaEntity();
        if (author.getId() != null && author.getId().getValue() != null) {
            entity.setId(author.getId().getValue());
        }
        entity.setName(author.getName().getValue());
        entity.setBiography(author.getBiography().getValue());
        entity.setDeleteFlg(author.isDeleted());

        return entity;
    }

    public Author toDomainEntity(AuthorJpaEntity jpaEntity) {

        Author author = Author.create(
                AuthorName.of(jpaEntity.getName()),
                Biography.of(jpaEntity.getBiography())
        );

        // Reflection để set ID (trong thực tế nên có setter package-private)
        try {
            java.lang.reflect.Field idField = Author.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(author, new AuthorId(jpaEntity.getId()));

            java.lang.reflect.Field deletedField = Author.class.getDeclaredField("deleted");
            deletedField.setAccessible(true);
            deletedField.set(author, jpaEntity.isDeleteFlg());

            // Clear events since this is loading from DB
            author.clearEvents();

        } catch (Exception e) {
            throw new RuntimeException("Error mapping JPA entity to domain entity", e);
        }

        return author;
    }
}

package com.library.book.infrastructure.persistence.mapper;

import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.model.author.AuthorName;
import com.library.book.domain.model.author.Biography;
import com.library.book.infrastructure.persistence.entity.AuthorEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthorEntityMapper {

    public AuthorEntity toJpaEntity(Author author) {

        AuthorEntity entity = new AuthorEntity();
        if (author.getId() != null && author.getId().getValue() != null) {
            entity.setId(author.getId().getValue());
        }
        entity.setName(author.getName().getValue());
        entity.setBiography(author.getBiography().getValue());
        entity.setDeleteFlg(author.isDeleted());

        return entity;
    }

    public Author toDomainEntity(AuthorEntity jpaEntity) {

        Author author = Author.create(
                AuthorName.of(jpaEntity.getName()),
                Biography.of(jpaEntity.getBiography()),
                "system" // Default user for reconstruction
        );
        try {
            author.setId(new AuthorId(jpaEntity.getId()));
            author.setDeleted(jpaEntity.isDeleteFlg());
            // Clear events since this is loading from DB
            author.clearEvents();
        } catch (Exception e) {
            throw new RuntimeException("Error mapping JPA entity to domain entity", e);
        }
        return author;
    }
}

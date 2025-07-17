package com.library.book.infrastructure.persistence.mapper;

import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.model.book.*;
import com.library.book.domain.model.category.CategoryId;
import com.library.book.domain.model.publisher.PublisherId;
import com.library.book.infrastructure.persistence.entity.BookEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BookEntityMapper {

    public BookEntity toJpaEntity(Book book) {
        BookEntity entity = new BookEntity();
        
        if (book.getId() != null && book.getId().getValue() != null) {
            entity.setId(book.getId().getValue());
        }
        
        entity.setTitle(book.getTitle().getValue());
        entity.setIsbn(book.getIsbn().getValue());
        
        if (book.getPublisherId() != null) {
            entity.setPublisherId(book.getPublisherId().getValue());
        }
        
        entity.setPublicationYear(book.getPublicationYear().getValue());
        entity.setDescription(book.getDescription().getValue());
        entity.setCoverImageUrl(book.getCoverImageUrl().getValue());
        
        entity.setAuthorIds(book.getAuthorIds().stream()
                .map(AuthorId::getValue)
                .collect(Collectors.toList()));
                
        entity.setCategoryIds(book.getCategoryIds().stream()
                .map(CategoryId::getValue)
                .collect(Collectors.toList()));
                
        entity.setDeleteFlg(book.isDeleted());

        return entity;
    }

    public Book toDomainEntity(BookEntity jpaEntity) {
        Book book = Book.create(
                BookTitle.of(jpaEntity.getTitle()),
                ISBN.of(jpaEntity.getIsbn()),
                new PublisherId(jpaEntity.getPublisherId()),
                jpaEntity.getPublicationYear() != null 
                        ? PublicationYear.of(jpaEntity.getPublicationYear())
                        : PublicationYear.empty(),
                jpaEntity.getDescription() != null 
                        ? Description.of(jpaEntity.getDescription())
                        : Description.empty(),
                jpaEntity.getCoverImageUrl() != null 
                        ? CoverImageUrl.of(jpaEntity.getCoverImageUrl())
                        : CoverImageUrl.empty(),
                jpaEntity.getAuthorIds().stream()
                        .map(AuthorId::new)
                        .collect(Collectors.toList()),
                jpaEntity.getCategoryIds().stream()
                        .map(CategoryId::new)
                        .collect(Collectors.toList())
        );

        // Reflection to set ID (in real-world, consider package-private setters)
        try {
            java.lang.reflect.Field idField = Book.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(book, new BookId(jpaEntity.getId()));

            java.lang.reflect.Field deletedField = Book.class.getDeclaredField("deleted");
            deletedField.setAccessible(true);
            deletedField.set(book, jpaEntity.isDeleteFlg());

            // Clear events since this is loading from DB
            book.clearEvents();
        } catch (Exception e) {
            throw new RuntimeException("Error mapping JPA entity to domain entity", e);
        }

        return book;
    }
} 
package com.library.user.infrastructure.persistence.mapper;

import com.library.user.domain.model.librarycard.CardNumber;
import com.library.user.domain.model.librarycard.CardStatus;
import com.library.user.domain.model.librarycard.LibraryCard;
import com.library.user.domain.model.librarycard.LibraryCardId;
import com.library.user.domain.model.user.UserId;
import com.library.user.infrastructure.persistence.entity.LibraryCardJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class LibraryCardEntityMapper {

    /**
     * Convert domain entity to JPA entity
     */
    public LibraryCardJpaEntity toJpaEntity(LibraryCard libraryCard) {
        LibraryCardJpaEntity entity = new LibraryCardJpaEntity();

        if (libraryCard.getId() != null && libraryCard.getId().getValue() != null) {
            entity.setId(libraryCard.getId().getValue());
        }

        entity.setUserId(libraryCard.getUserId().getValue());
        entity.setCardNumber(libraryCard.getCardNumber().getValue());
        entity.setIssueDate(libraryCard.getIssueDate());
        entity.setExpiryDate(libraryCard.getExpiryDate());
        entity.setStatus(libraryCard.getStatus().name());

        return entity;
    }

    /**
     * Convert JPA entity to domain entity
     */
    public LibraryCard toDomainEntity(LibraryCardJpaEntity entity) {
        return LibraryCard.reconstitute(
                LibraryCardId.of(entity.getId()),
                UserId.of(entity.getUserId()),
                CardNumber.of(entity.getCardNumber()),
                entity.getIssueDate(),
                entity.getExpiryDate(),
                CardStatus.valueOf(entity.getStatus())
        );
    }
}
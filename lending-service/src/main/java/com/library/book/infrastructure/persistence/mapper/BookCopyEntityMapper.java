package com.library.book.infrastructure.persistence.mapper;

import com.library.book.domain.model.bookcopy.*;
import com.library.book.infrastructure.persistence.entity.BookCopyJpaEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BookCopyEntityMapper {

    /**
     * Convert domain entity to JPA entity
     */
    public BookCopyJpaEntity toJpaEntity(BookCopy bookCopy) {
        BookCopyJpaEntity jpaEntity = new BookCopyJpaEntity();
        
        // Set ID if not null (for updates)
        if (bookCopy.getId() != null && bookCopy.getId().getValue() != null) {
            jpaEntity.setId(bookCopy.getId().getValue());
        }
        
        // Set book reference data
        jpaEntity.setBookId(bookCopy.getBookReference().getBookId());
        jpaEntity.setBookTitle(bookCopy.getBookReference().getTitle());
        
        // Set other properties
        jpaEntity.setCopyNumber(bookCopy.getCopyNumber().getValue());
        jpaEntity.setStatus(bookCopy.getStatus().name());
        
        if (bookCopy.getCondition() != null) {
            jpaEntity.setCondition(bookCopy.getCondition().name());
        }
        
        jpaEntity.setLocation(bookCopy.getLocation().getValue());
        jpaEntity.setDeleteFlg(bookCopy.isDeleted());
        
        // Set audit fields for new entities
        if (jpaEntity.getId() == null) {
            jpaEntity.setCreatedAt(LocalDateTime.now());
            jpaEntity.setCreatedBy("system");
        }
        
        jpaEntity.setUpdatedAt(LocalDateTime.now());
        jpaEntity.setUpdatedBy("system");
        
        return jpaEntity;
    }
    
    /**
     * Convert JPA entity to domain entity
     */
    public BookCopy toDomainEntity(BookCopyJpaEntity jpaEntity) {
        // Create value objects
        BookCopyId id = BookCopyId.of(jpaEntity.getId());
        BookReference bookReference = BookReference.of(jpaEntity.getBookId(), jpaEntity.getBookTitle());
        CopyNumber copyNumber = CopyNumber.of(jpaEntity.getCopyNumber());
        BookCopyStatus status = BookCopyStatus.valueOf(jpaEntity.getStatus());
        
        // Optional value objects
        BookCopyCondition condition = null;
        if (jpaEntity.getCondition() != null && !jpaEntity.getCondition().isEmpty()) {
            condition = BookCopyCondition.valueOf(jpaEntity.getCondition());
        }
        
        Location location = jpaEntity.getLocation() != null && !jpaEntity.getLocation().isEmpty() 
            ? Location.of(jpaEntity.getLocation())
            : Location.empty();
        
        // Reconstitute the domain entity
        return BookCopy.reconstitute(
            id,
            bookReference,
            copyNumber,
            status,
            condition,
            location,
            jpaEntity.isDeleteFlg()
        );
    }
} 
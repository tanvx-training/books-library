package com.library.book.application.mapper;

import com.library.book.application.dto.response.BookCopyResponse;
import com.library.book.domain.model.bookcopy.BookCopy;
import com.library.book.infrastructure.persistence.entity.BookCopyJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class BookCopyDtoMapper {

    /**
     * Convert domain entity to response DTO
     */
    public BookCopyResponse toResponseDto(BookCopy bookCopy) {
        return BookCopyResponse.builder()
                .id(bookCopy.getId().getValue())
                .bookId(bookCopy.getBookReference().getBookId())
                .bookTitle(bookCopy.getBookReference().getTitle())
                .copyNumber(bookCopy.getCopyNumber().getValue())
                .status(bookCopy.getStatus().name())
                .condition(bookCopy.getCondition() != null ? bookCopy.getCondition().name() : null)
                .location(bookCopy.getLocation().getValue())
                .build();
    }
    
    /**
     * Convert JPA entity to response DTO (for queries)
     */
    public BookCopyResponse toResponseDto(BookCopyJpaEntity jpaEntity) {
        return BookCopyResponse.builder()
                .id(jpaEntity.getId())
                .bookId(jpaEntity.getBookId())
                .bookTitle(jpaEntity.getBookTitle())
                .copyNumber(jpaEntity.getCopyNumber())
                .status(jpaEntity.getStatus())
                .condition(jpaEntity.getCondition())
                .location(jpaEntity.getLocation())
                .createdAt(jpaEntity.getCreatedAt())
                .updatedAt(jpaEntity.getUpdatedAt())
                .build();
    }
} 
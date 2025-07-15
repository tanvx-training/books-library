package com.library.book.infrastructure.persistence.mapper;

import com.library.book.domain.model.book.BookId;
import com.library.book.domain.model.bookcopy.*;
import com.library.book.infrastructure.persistence.entity.BookCopyJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper between BookCopy domain model and JPA entity
 */
@Mapper(componentModel = "spring")
public interface BookCopyEntityMapper {
    
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "bookId", source = "bookId.value")
    @Mapping(target = "copyNumber", source = "copyNumber.value")
    @Mapping(target = "location", source = "location.value")
    @Mapping(target = "deleteFlg", source = "deleted")
    BookCopyJpaEntity toJpaEntity(BookCopy bookCopy);
    
    @Mapping(target = "id", source = "id", qualifiedByName = "mapToBookCopyId")
    @Mapping(target = "bookId", source = "bookId", qualifiedByName = "mapToBookId")
    @Mapping(target = "copyNumber", source = "copyNumber", qualifiedByName = "mapToCopyNumber")
    @Mapping(target = "location", source = "location", qualifiedByName = "mapToLocation")
    @Mapping(target = "deleted", source = "deleteFlg")
    BookCopy toDomainEntity(BookCopyJpaEntity entity);
    
    @Named("mapToBookCopyId")
    default BookCopyId mapToBookCopyId(Long id) {
        return id != null ? new BookCopyId(id) : null;
    }
    
    @Named("mapToBookId")
    default BookId mapToBookId(Long bookId) {
        return bookId != null ? new BookId(bookId) : null;
    }
    
    @Named("mapToCopyNumber")
    default CopyNumber mapToCopyNumber(String copyNumber) {
        return copyNumber != null ? CopyNumber.of(copyNumber) : null;
    }
    
    @Named("mapToLocation")
    default Location mapToLocation(String location) {
        return location != null ? Location.of(location) : Location.empty();
    }
}
package com.library.book.util.mapper;

import com.library.book.domain.enums.BookCopyStatus;
import com.library.book.domain.model.BookCopy;
import com.library.book.presentation.dto.request.BookCopyRequestDTO;
import com.library.book.presentation.dto.response.BookCopyResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface BookCopyMapper {
    BookCopyResponseDTO toDto(BookCopy bookCopy);
    
    @Mapping(target = "status", source = "status", defaultExpression = "java(com.library.book.domain.enums.BookCopyStatus.AVAILABLE.name())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "borrowings", ignore = true)
    BookCopy toEntity(BookCopyRequestDTO bookCopyRequestDTO);
}
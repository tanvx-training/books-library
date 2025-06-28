package com.library.book.utils.mapper;

import com.library.book.model.BookCopy;
import com.library.book.dto.request.BookCopyRequestDTO;
import com.library.book.dto.response.BookCopyResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookCopyMapper {
    BookCopyResponseDTO toDto(BookCopy bookCopy);
    
    @Mapping(target = "status", source = "status", defaultExpression = "java(com.library.book.utils.enums.BookCopyStatus.AVAILABLE.name())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "borrowings", ignore = true)
    BookCopy toEntity(BookCopyRequestDTO bookCopyRequestDTO);
}
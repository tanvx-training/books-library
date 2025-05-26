package com.library.book.util.mapper;

import com.library.book.domain.model.BookCopy;
import com.library.book.presentation.dto.request.BookCopyRequestDTO;
import com.library.book.presentation.dto.response.BookCopyResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookCopyMapper {
    BookCopyResponseDTO toDto(BookCopy bookCopy);
    @Mapping(target = "status", constant = "AVAILABLE")
    BookCopy toEntity(BookCopyRequestDTO bookCopyRequestDTO);
}
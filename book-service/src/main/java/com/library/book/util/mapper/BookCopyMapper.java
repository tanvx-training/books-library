package com.library.book.util.mapper;

import com.library.book.domain.model.BookCopy;
import com.library.book.presentation.dto.response.BookCopyResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookCopyMapper {
    BookCopyResponseDTO toDto(BookCopy bookCopy);
}
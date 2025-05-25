package com.library.book.util.mapper;

import com.library.book.domain.model.Author;
import com.library.book.presentation.dto.request.AuthorCreateDTO;
import com.library.book.presentation.dto.response.AuthorResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    AuthorResponseDTO toDto(Author author);
    Author toEntity(AuthorCreateDTO authorCreateDTO);
}
package com.library.book.utils.mapper;

import com.library.book.model.Author;
import com.library.book.dto.request.AuthorCreateDTO;
import com.library.book.dto.response.AuthorResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    AuthorResponseDTO toDto(Author author);
    Author toEntity(AuthorCreateDTO authorCreateDTO);
}
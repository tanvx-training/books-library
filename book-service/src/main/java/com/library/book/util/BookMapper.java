package com.library.book.util;

import com.library.book.domain.model.Book;
import com.library.book.presentation.dto.request.BookCreateDTO;
import com.library.book.presentation.dto.request.BookUpdateDTO;
import com.library.book.presentation.dto.response.BookResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {CategoryMapper.class})
public interface BookMapper {

    @Mapping(target = "category", ignore = true)
    Book toEntity(BookCreateDTO dto);

    BookResponseDTO toDto(Book entity);

    @Mapping(target = "category", ignore = true)
    void updateEntityFromDto(BookUpdateDTO dto, @MappingTarget Book entity);
}

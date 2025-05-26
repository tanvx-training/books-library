package com.library.book.util.mapper;

import com.library.book.domain.model.Book;
import com.library.book.presentation.dto.request.BookCreateDTO;
import com.library.book.presentation.dto.response.BookResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PublisherMapper.class, AuthorMapper.class, CategoryMapper.class, BookCopyMapper.class})
public interface BookMapper {
    BookResponseDTO toDto(Book book);

    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "bookCopies", source = "bookCopies")
    Book toEntity(BookCreateDTO dto);
}

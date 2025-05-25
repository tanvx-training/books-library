package com.library.book.util.mapper;

import com.library.book.domain.model.Book;
import com.library.book.presentation.dto.response.BookResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PublisherMapper.class, AuthorMapper.class, CategoryMapper.class, BookCopyMapper.class})
public interface BookMapper {
    @Mapping(source = "bookCopies", target = "copies")
    BookResponseDTO toDto(Book book);
}

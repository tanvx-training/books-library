package com.library.book.utils.mapper;

import com.library.book.model.Publisher;
import com.library.book.dto.request.PublisherCreateDTO;
import com.library.book.dto.response.PublisherResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PublisherMapper {
    PublisherResponseDTO toDto(Publisher publisher);
    @Mapping(target = "books", ignore = true)
    Publisher toEntity(PublisherCreateDTO publisherCreateDTO);
}
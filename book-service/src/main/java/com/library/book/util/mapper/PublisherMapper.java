package com.library.book.util.mapper;

import com.library.book.domain.model.Publisher;
import com.library.book.presentation.dto.request.PublisherCreateDTO;
import com.library.book.presentation.dto.response.PublisherResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PublisherMapper {
    PublisherResponseDTO toDto(Publisher publisher);
    Publisher toEntity(PublisherCreateDTO publisherCreateDTO);
}
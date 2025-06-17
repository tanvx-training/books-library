package com.library.user.util.mapper;

import com.library.user.domain.model.LibraryCard;
import com.library.user.presentation.dto.response.LibraryCardResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LibraryCardMapper {

    @Mapping(target = "userId", expression = "java(libraryCard.getUser().getId())")
    LibraryCardResponseDTO toLibraryCardResponseDTO(LibraryCard libraryCard);
}

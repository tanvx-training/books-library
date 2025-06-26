package com.library.user.util.mapper;

import com.library.user.domain.model.LibraryCard;
import com.library.user.presentation.dto.response.LibraryCardResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LibraryCardMapper {

    @Mapping(target = "username", expression = "java(libraryCard.getUser().getUsername())")
    @Mapping(target = "firstName", expression = "java(libraryCard.getUser().getFirstName())")
    @Mapping(target = "lastName", expression = "java(libraryCard.getUser().getLastName())")
    LibraryCardResponseDTO toLibraryCardResponseDTO(LibraryCard libraryCard);
}

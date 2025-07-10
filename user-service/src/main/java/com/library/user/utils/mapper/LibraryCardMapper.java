package com.library.user.utils.mapper;

import com.library.user.infrastructure.persistence.entity.LibraryCard;
import com.library.user.dto.response.LibraryCardResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LibraryCardMapper {

    @Mapping(target = "username", expression = "java(libraryCard.getUser().getUsername())")
    @Mapping(target = "firstName", expression = "java(libraryCard.getUser().getFirstName())")
    @Mapping(target = "lastName", expression = "java(libraryCard.getUser().getLastName())")
    LibraryCardResponseDTO toLibraryCardResponseDTO(LibraryCard libraryCard);
}

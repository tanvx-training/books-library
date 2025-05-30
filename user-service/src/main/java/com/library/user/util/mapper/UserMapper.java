package com.library.user.util.mapper;

import com.library.user.domain.model.User;
import com.library.user.presentation.dto.request.RegisterRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toEntity(RegisterRequestDTO registerRequestDTO);
}

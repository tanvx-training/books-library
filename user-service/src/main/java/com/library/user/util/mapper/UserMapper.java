package com.library.user.util.mapper;

import com.library.user.domain.model.User;
import com.library.user.presentation.dto.request.RegisterRequestDTO;
import com.library.user.presentation.dto.response.UserDetailResponseDTO;
import com.library.user.presentation.dto.response.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {LibraryCardMapper.class})
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toEntity(RegisterRequestDTO registerRequestDTO);
    @Mapping(target = "isActive", expression = "java(!user.isDeleteFlg())")
    UserResponseDTO toUserResponseDTO(User user);
    @Mapping(target = "isActive", expression = "java(!user.isDeleteFlg())")
    @Mapping(source = "libraryCards", target = "cards")
    UserDetailResponseDTO toUserDetailResponseDTO(User user);
}

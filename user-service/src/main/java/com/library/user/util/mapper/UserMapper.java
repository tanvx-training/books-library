package com.library.user.util.mapper;

import com.library.user.domain.model.User;
import com.library.user.presentation.dto.request.RegisterRequestDTO;
import lombok.RequiredArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
@RequiredArgsConstructor
public abstract class UserMapper {

    private final PasswordEncoder passwordEncoder;

    @Mapping(target = "password", ignore = true)
    public abstract User toEntity(RegisterRequestDTO registerRequestDTO);

    @AfterMapping
    protected void encodePassword(RegisterRequestDTO dto, @MappingTarget User user) {
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
    }
}

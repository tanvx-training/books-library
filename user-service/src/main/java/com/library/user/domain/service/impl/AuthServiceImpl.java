package com.library.user.domain.service.impl;

import com.library.common.exception.ResourceExistedException;
import com.library.common.exception.ResourceNotFoundException;
import com.library.user.domain.model.Role;
import com.library.user.domain.model.User;
import com.library.user.domain.service.AuthService;
import com.library.user.enums.UserRole;
import com.library.user.infrastructure.repository.RoleRepository;
import com.library.user.infrastructure.repository.UserRepository;
import com.library.user.presentation.dto.request.RegisterRequestDTO;
import com.library.user.presentation.dto.response.RegisterResponseDTO;
import com.library.user.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RegisterResponseDTO registerUser(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByUsername(registerRequestDTO.getUsername())) {
            throw new ResourceExistedException("User", "username", registerRequestDTO.getUsername());
        }
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new ResourceExistedException("User", "email", registerRequestDTO.getEmail());
        }
        User user = userMapper.toEntity(registerRequestDTO);
        Role readerRole = roleRepository.findByName(UserRole.READER.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", UserRole.READER.name()));
        user.setRoles(List.of(readerRole));
        return null;
    }
}

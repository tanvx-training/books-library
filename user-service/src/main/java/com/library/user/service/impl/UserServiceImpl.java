package com.library.user.service.impl;

import com.library.common.dto.PaginatedRequest;
import com.library.common.aop.exception.ResourceNotFoundException;
import com.library.common.dto.PaginatedResponse;
import com.library.user.service.UserService;
import com.library.user.repository.UserRepository;
import com.library.user.dto.response.UserDetailResponseDTO;
import com.library.user.dto.response.UserResponseDTO;
import com.library.user.utils.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<UserResponseDTO> getAllUsers(PaginatedRequest paginatedRequest) {
        Pageable pageable = paginatedRequest.toPageable();
        Page<UserResponseDTO> page = userRepository.findAll(pageable)
                .map(userMapper::toUserResponseDTO);
        return PaginatedResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponseDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserDetailResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}

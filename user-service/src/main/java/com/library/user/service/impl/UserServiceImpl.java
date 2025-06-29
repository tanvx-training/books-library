package com.library.user.service.impl;

import com.library.common.aop.annotation.Loggable;
import com.library.common.dto.PaginatedRequest;
import com.library.common.aop.exception.ResourceNotFoundException;
import com.library.common.dto.PaginatedResponse;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
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
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "User",
        logArguments = true,
        logReturnValue = false, // Don't log full collection in service layer
        logExecutionTime = true,
        performanceThresholdMs = 800L,
        messagePrefix = "USER_SERVICE_LIST",
        customTags = {"layer=service", "transaction=readonly", "pagination=true"}
    )
    public PaginatedResponse<UserResponseDTO> getAllUsers(PaginatedRequest paginatedRequest) {
        Pageable pageable = paginatedRequest.toPageable();
        Page<UserResponseDTO> page = userRepository.findAll(pageable)
                .map(userMapper::toUserResponseDTO);
        return PaginatedResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.READ,
        resourceType = "User",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 300L,
        messagePrefix = "USER_SERVICE_DETAIL",
        customTags = {"layer=service", "transaction=readonly", "single_entity=true", "includes_mapping=true"}
    )
    public UserDetailResponseDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserDetailResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}

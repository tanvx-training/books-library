package com.library.user.service;

import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;
import com.library.user.dto.response.UserDetailResponseDTO;
import com.library.user.dto.response.UserResponseDTO;

public interface UserService {

    PaginatedResponse<UserResponseDTO> getAllUsers(PaginatedRequest paginatedRequest);

    UserDetailResponseDTO getUserById(Long userId);
}

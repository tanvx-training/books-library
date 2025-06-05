package com.library.user.domain.service;

import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import com.library.user.presentation.dto.response.UserDetailResponseDTO;
import com.library.user.presentation.dto.response.UserResponseDTO;

public interface UserService {

    PageResponseDTO<UserResponseDTO> getAllUsers(PageRequestDTO pageRequestDTO);

    UserDetailResponseDTO getUserById(Long userId);
}

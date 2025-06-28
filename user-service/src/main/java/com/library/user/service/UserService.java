package com.library.user.service;

import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import com.library.user.dto.response.UserDetailResponseDTO;
import com.library.user.dto.response.UserResponseDTO;

public interface UserService {

    PageResponseDTO<UserResponseDTO> getAllUsers(PageRequestDTO pageRequestDTO);

    UserDetailResponseDTO getUserById(Long userId);
}

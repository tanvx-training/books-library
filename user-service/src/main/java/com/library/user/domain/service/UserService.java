package com.library.user.domain.service;

import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import com.library.user.presentation.dto.response.UserResponseDTO;
import jakarta.validation.Valid;

public interface UserService {

    PageResponseDTO<UserResponseDTO> getAllUsers(PageRequestDTO pageRequestDTO);
}

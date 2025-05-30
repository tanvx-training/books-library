package com.library.user.domain.service;

import com.library.user.presentation.dto.request.RegisterRequestDTO;
import com.library.user.presentation.dto.response.RegisterResponseDTO;
import jakarta.validation.Valid;

public interface AuthService {

    RegisterResponseDTO registerUser(RegisterRequestDTO registerRequestDTO);
}

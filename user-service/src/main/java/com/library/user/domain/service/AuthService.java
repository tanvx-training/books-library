package com.library.user.domain.service;

import com.library.user.presentation.dto.request.LoginRequestDTO;
import com.library.user.presentation.dto.request.RegisterRequestDTO;
import com.library.user.presentation.dto.response.LoginResponseDTO;
import com.library.user.presentation.dto.response.RegisterResponseDTO;

public interface AuthService {

    RegisterResponseDTO registerUser(RegisterRequestDTO registerRequestDTO);

    LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO);
}

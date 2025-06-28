package com.library.user.service;

import com.library.user.dto.request.LoginRequestDTO;
import com.library.user.dto.request.RegisterRequestDTO;
import com.library.user.dto.response.LoginResponseDTO;
import com.library.user.dto.response.RegisterResponseDTO;

public interface AuthService {

    RegisterResponseDTO registerUser(RegisterRequestDTO registerRequestDTO);

    LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO);
}

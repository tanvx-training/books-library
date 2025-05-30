package com.library.user.domain.service.impl;

import com.library.common.exception.ResourceExistedException;
import com.library.common.exception.ResourceNotFoundException;
import com.library.user.domain.model.RefreshToken;
import com.library.user.domain.model.Role;
import com.library.user.domain.model.User;
import com.library.user.domain.service.AuthService;
import com.library.user.domain.enums.UserRole;
import com.library.user.domain.service.RefreshTokenService;
import com.library.user.infrastructure.repository.RoleRepository;
import com.library.user.infrastructure.repository.UserRepository;
import com.library.user.presentation.dto.request.LoginRequestDTO;
import com.library.user.presentation.dto.request.RegisterRequestDTO;
import com.library.user.presentation.dto.response.LoginResponseDTO;
import com.library.user.presentation.dto.response.RegisterResponseDTO;
import com.library.user.util.mapper.UserMapper;
import com.library.user.util.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final RefreshTokenService refreshTokenService;

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
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        Role readerRole = roleRepository.findByName(UserRole.READER.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", UserRole.READER.name()));
        user.setRoles(List.of(readerRole));
        userRepository.save(user);
        return RegisterResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .build();
    }

    @Override
    @Transactional
    public LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getUsername(),
                            loginRequestDTO.getPassword()
                    )
            );

            // Generate JWT access token
            Objects.requireNonNull(authentication);
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);

            // Create refresh token and save to database
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(
                    loginRequestDTO.getUsername());

            List<String> roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            return LoginResponseDTO.builder()
                    .username(authentication.getName())
                    .roles(roles)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .build();
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
